package com.medsy.data.aichat.mapper

import com.medsy.data.aichat.mock.MockAiChatEntry
import com.medsy.data.aichat.mock.MockAiChatSender
import com.medsy.data.aichat.mock.MockAiChatState
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatSender
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.aichat.model.AiMedicine
import com.medsy.domain.aichat.model.AiPharmacy

fun MockAiChatState.toDomain(): AiChatSession = AiChatSession(
    messages = entries.map(MockAiChatEntry::toDomain),
    isResponding = isResponding,
    isAiCallInProgress = isAiCallInProgress,
    isReminderConfirmed = isReminderConfirmed,
)

private fun MockAiChatEntry.toDomain(): AiChatMessage {
    val content = when (sender) {
        MockAiChatSender.USER_SCENARIO ->
            AiChatContent.UserScenario(requireNotNull(scenario))

        MockAiChatSender.USER_TEXT -> AiChatContent.UserText(text.orEmpty())
        MockAiChatSender.ASSISTANT -> AiChatContent.AssistantResponse(
            scenario = requireNotNull(scenario),
            medicines = medicines.map {
                AiMedicine(
                    productId = it.productId,
                    name = it.name,
                    activeIngredient = it.activeIngredient,
                    priceEgp = it.priceEgp,
                    confidencePercent = it.confidencePercent,
                )
            },
            pharmacies = pharmacies.map {
                AiPharmacy(
                    name = it.name,
                    distanceKm = it.distanceKm,
                    phoneNumber = it.phoneNumber,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    isOpen = it.isOpen,
                )
            },
        )
    }
    return AiChatMessage(
        id = id,
        sender = if (sender == MockAiChatSender.ASSISTANT) {
            AiChatSender.ASSISTANT
        } else {
            AiChatSender.USER
        },
        content = content,
    )
}
