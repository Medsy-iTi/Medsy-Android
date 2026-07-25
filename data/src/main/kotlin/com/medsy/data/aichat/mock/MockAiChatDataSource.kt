package com.medsy.data.aichat.mock

import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.model.AiChatImageKind
import com.medsy.domain.aichat.model.AiChatScenario
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Singleton
class MockAiChatDataSource @Inject constructor() {
    private val _state = MutableStateFlow(MockAiChatState())
    val state: StateFlow<MockAiChatState> = _state.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var nextMessageId = 1L
    private var sessionGeneration = 0L

    suspend fun submit(
        action: AiChatAction,
    ): EmptyMedsyResult<MedsyError.Local> {
        return try {
            when (action) {
                AiChatAction.StartAiCall -> {
                    _state.update { it.copy(isAiCallInProgress = true) }
                    return MedsyResult.Success(Unit)
                }
                AiChatAction.EndAiCall -> {
                    _state.update { it.copy(isAiCallInProgress = false) }
                    return MedsyResult.Success(Unit)
                }
                AiChatAction.ConfirmReminder -> {
                    _state.update { it.copy(isReminderConfirmed = true) }
                    return MedsyResult.Success(Unit)
                }
                else -> Unit
            }
            if (_state.value.isResponding) return MedsyResult.Success(Unit)

            val scenario = when (action) {
                is AiChatAction.SelectScenario -> {
                    addUserScenario(action.scenario)
                    action.scenario
                }

                is AiChatAction.SendText -> {
                    addUserText(action.text)
                    if (action.text.containsEmergencyRedFlag()) {
                        AiChatScenario.EMERGENCY
                    } else {
                        AiChatScenario.UNSUPPORTED
                    }
                }

                is AiChatAction.ImageSelected -> {
                    val imageScenario = when (action.kind) {
                        AiChatImageKind.PRESCRIPTION -> AiChatScenario.PRESCRIPTION_IMAGE
                        AiChatImageKind.MEDICINE -> AiChatScenario.MEDICINE_IMAGE
                    }
                    addUserScenario(imageScenario)
                    imageScenario
                }
                AiChatAction.StartAiCall,
                AiChatAction.EndAiCall,
                AiChatAction.ConfirmReminder -> error("Handled before scenario dispatch")
            }

            _state.update { it.copy(isResponding = true) }
            val generation = sessionGeneration
            scope.launch {
                delay(RESPONSE_DELAY_MILLIS)
                if (generation == sessionGeneration) {
                    addAssistantResponse(scenario)
                }
            }
            MedsyResult.Success(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            _state.update { it.copy(isResponding = false) }
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }

    fun reset() {
        sessionGeneration += 1L
        nextMessageId = 1L
        _state.value = MockAiChatState()
    }

    private fun addUserScenario(scenario: AiChatScenario) {
        appendEntry(
            MockAiChatEntry(
                id = nextMessageId++,
                sender = MockAiChatSender.USER_SCENARIO,
                scenario = scenario,
            )
        )
    }

    private fun addUserText(text: String) {
        appendEntry(
            MockAiChatEntry(
                id = nextMessageId++,
                sender = MockAiChatSender.USER_TEXT,
                text = text,
            )
        )
    }

    private fun addAssistantResponse(scenario: AiChatScenario) {
        appendEntry(
            MockAiChatEntry(
                id = nextMessageId++,
                sender = MockAiChatSender.ASSISTANT,
                scenario = scenario,
                medicines = medicinesFor(scenario),
                pharmacies = pharmaciesFor(scenario),
            ),
            isResponding = false,
        )
    }

    private fun appendEntry(
        entry: MockAiChatEntry,
        isResponding: Boolean = _state.value.isResponding,
    ) {
        _state.update { current ->
            current.copy(
                entries = current.entries + entry,
                isResponding = isResponding,
            )
        }
    }

    private fun medicinesFor(scenario: AiChatScenario): List<MockAiMedicine> = when (scenario) {
        AiChatScenario.HEADACHE_TRIAGE,
        AiChatScenario.MEDICINE_INFORMATION,
        AiChatScenario.MEDICINE_IMAGE -> listOf(PANADOL_EXTRA)

        AiChatScenario.PRESCRIPTION_IMAGE,
        AiChatScenario.REORDER_MEDICINES -> listOf(AMLODIPINE, LIPITOR)

        AiChatScenario.CHEAPER_EQUIVALENT -> listOf(LIPITOR, ATOR)
        else -> emptyList()
    }

    private fun pharmaciesFor(scenario: AiChatScenario): List<MockAiPharmacy> =
        if (scenario == AiChatScenario.NEARBY_PHARMACIES) PHARMACIES else emptyList()

    private fun String.containsEmergencyRedFlag(): Boolean {
        val normalized = lowercase()
        return EMERGENCY_TERMS.any(normalized::contains)
    }

    private companion object {
        const val RESPONSE_DELAY_MILLIS = 850L

        val PANADOL_EXTRA = MockAiMedicine(
            productId = 1,
            name = "Panadol Extra",
            activeIngredient = "Paracetamol + caffeine",
            priceEgp = 52,
            confidencePercent = 98,
        )
        val AMLODIPINE = MockAiMedicine(
            productId = 2,
            name = "Amlodipine 5 mg",
            activeIngredient = "Amlodipine",
            priceEgp = 75,
            confidencePercent = 96,
        )
        val LIPITOR = MockAiMedicine(
            productId = 3,
            name = "Lipitor 20 mg",
            activeIngredient = "Atorvastatin",
            priceEgp = 210,
            confidencePercent = 91,
        )
        val ATOR = MockAiMedicine(
            productId = 4,
            name = "Ator 20 mg",
            activeIngredient = "Atorvastatin",
            priceEgp = 88,
        )

        val PHARMACIES = listOf(
            MockAiPharmacy(
                name = "El Ezaby Pharmacy",
                distanceKm = 0.8,
                phoneNumber = "19600",
                latitude = 29.9602,
                longitude = 31.2577,
                isOpen = true,
            ),
            MockAiPharmacy(
                name = "19011 Pharmacy",
                distanceKm = 1.4,
                phoneNumber = "19011",
                latitude = 29.9557,
                longitude = 31.2693,
                isOpen = true,
            ),
        )

        val EMERGENCY_TERMS = listOf(
            "chest pain",
            "left arm",
            "can't breathe",
            "cannot breathe",
            "unconscious",
            "severe bleeding",
            "ألم في الصدر",
            "مش قادر يتنفس",
            "نزيف شديد",
        )
    }
}
