package com.medsy.domain.aichat.model

data class AiChatSession(
    val messages: List<AiChatMessage> = emptyList(),
    val isResponding: Boolean = false,
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
    data class UserText(val value: String) : AiChatContent

    data class AssistantResponse(
        val answer: String,
        val products: List<AiCatalogProduct>,
    ) : AiChatContent
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

data class AiChatAction(
    val question: String,
    val language: AiChatLanguage,
    val limit: Int,
)

enum class AiChatLanguage {
    ENGLISH,
    ARABIC,
}
