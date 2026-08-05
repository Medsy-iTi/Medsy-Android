package com.medsy.domain.aichat.model

import com.medsy.domain.prescription.model.PrescriptionImage

data class AiChatSession(
    val messages: List<AiChatMessage> = emptyList(),
    val isResponding: Boolean = false,
    val isHydrated: Boolean = false,
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
    data class UserText(
        val value: String,
        val imageUri: String? = null,
    ) : AiChatContent

    data class AssistantMessage(
        val answer: String,
        val intent: AiChatIntent,
        val products: List<AiCatalogProduct>,
        val doctorSpecializations: List<String>,
        val emergencyNumbers: List<AiEmergencyNumber>,
        val categories: List<AiChatCategory>,
        val disclaimer: String?,
        val action: AiChatMessageAction?,
    ) : AiChatContent
}

enum class AiChatIntent {
    GREETING,
    MEDICINE_REQUEST,
    SYMPTOM_ADVICE,
    DOCTOR_SPECIALIZATION,
    EMERGENCY,
    MEDICINE_USAGE,
    CATEGORY_BROWSE,
    ADD_TO_CART,
    CREATE_REQUEST,
    SET_REMINDER,
    DELETE_REMINDER,
    LIST_REMINDERS,
    OTHER,
}

data class AiEmergencyNumber(
    val service: AiEmergencyService,
    val number: String,
)

enum class AiEmergencyService {
    AMBULANCE,
    POLICE,
    FIRE,
}

data class AiChatCategory(
    val id: Int,
    val name: String,
)

sealed interface AiChatMessageAction {
    data class AddedToCart(
        val quantity: Int,
        val cartItemCount: Long?,
    ) : AiChatMessageAction

    data object CreateRequest : AiChatMessageAction
}

sealed interface AiChatOutgoingMessage {
    data class Text(val message: String) : AiChatOutgoingMessage

    data class Image(
        val image: PrescriptionImage,
        val caption: String,
    ) : AiChatOutgoingMessage
}

data class AiCatalogProduct(
    val productId: Int,
    val name: String,
    val productName: String,
    val scientificName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val priceEgp: Double?,
    val company: String?,
    val route: String?,
    val description: String?,
    val imageUrl: String?,
)
