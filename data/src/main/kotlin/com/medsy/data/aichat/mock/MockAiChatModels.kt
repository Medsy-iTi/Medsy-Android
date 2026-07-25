package com.medsy.data.aichat.mock

import com.medsy.domain.aichat.model.AiChatScenario

data class MockAiChatState(
    val entries: List<MockAiChatEntry> = emptyList(),
    val isResponding: Boolean = false,
    val isAiCallInProgress: Boolean = false,
    val isReminderConfirmed: Boolean = false,
)

data class MockAiChatEntry(
    val id: Long,
    val sender: MockAiChatSender,
    val scenario: AiChatScenario? = null,
    val text: String? = null,
    val medicines: List<MockAiMedicine> = emptyList(),
    val pharmacies: List<MockAiPharmacy> = emptyList(),
)

enum class MockAiChatSender {
    USER_SCENARIO,
    USER_TEXT,
    ASSISTANT,
}

data class MockAiMedicine(
    val productId: Int,
    val name: String,
    val activeIngredient: String,
    val priceEgp: Int,
    val confidencePercent: Int? = null,
)

data class MockAiPharmacy(
    val name: String,
    val distanceKm: Double,
    val phoneNumber: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen: Boolean,
)
