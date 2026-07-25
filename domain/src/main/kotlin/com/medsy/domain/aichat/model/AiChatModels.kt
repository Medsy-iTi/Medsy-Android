package com.medsy.domain.aichat.model

data class AiChatSession(
    val messages: List<AiChatMessage> = emptyList(),
    val isResponding: Boolean = false,
    val isAiCallInProgress: Boolean = false,
    val isReminderConfirmed: Boolean = false,
)

data class AiChatMessage(
    val id: Long,
    val sender: AiChatSender,
    val content: AiChatContent,
)

enum class AiChatSender {
    USER,
    ASSISTANT,
}

sealed interface AiChatContent {
    data class UserScenario(val scenario: AiChatScenario) : AiChatContent
    data class UserText(val value: String) : AiChatContent
    data class AssistantResponse(
        val scenario: AiChatScenario,
        val medicines: List<AiMedicine> = emptyList(),
        val pharmacies: List<AiPharmacy> = emptyList(),
    ) : AiChatContent
}

enum class AiChatScenario {
    HEADACHE_TRIAGE,
    NEARBY_PHARMACIES,
    PRESCRIPTION_IMAGE,
    MEDICINE_IMAGE,
    MEDICINE_INFORMATION,
    INTERACTION_CHECK,
    DOSE_REMINDER,
    REORDER_MEDICINES,
    ORDER_TRACKING,
    CHEAPER_EQUIVALENT,
    EMERGENCY,
    UNREADABLE_IMAGE,
    NO_MEDICINE_FOUND,
    UNSUPPORTED,
}

enum class AiChatImageKind {
    PRESCRIPTION,
    MEDICINE,
}

data class AiMedicine(
    val productId: Int,
    val name: String,
    val activeIngredient: String,
    val priceEgp: Int,
    val confidencePercent: Int? = null,
)

data class AiPharmacy(
    val name: String,
    val distanceKm: Double,
    val phoneNumber: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen: Boolean,
)

sealed interface AiChatAction {
    data class SelectScenario(val scenario: AiChatScenario) : AiChatAction
    data class SendText(val text: String) : AiChatAction
    data class ImageSelected(
        val kind: AiChatImageKind,
        val uri: String,
    ) : AiChatAction
    data object StartAiCall : AiChatAction
    data object EndAiCall : AiChatAction
    data object ConfirmReminder : AiChatAction
}
