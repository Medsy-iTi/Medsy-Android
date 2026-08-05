package com.medsy.data.aichat.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageRequestDto(
    val message: String,
)

@JsonClass(generateAdapter = true)
data class ChatMessageResponseDto(
    val conversationId: Long? = null,
    val messageId: Long? = null,
    val intent: String? = null,
    val answer: String? = null,
    val products: List<ChatProductDto>? = null,
    val alternatives: List<ChatProductDto>? = null,
    val doctorSpecializations: List<String>? = null,
    val emergencyNumbers: List<EmergencyNumberDto>? = null,
    val categories: List<ChatCategoryDto>? = null,
    val disclaimer: String? = null,
    val action: ChatActionDto? = null,
)

@JsonClass(generateAdapter = true)
data class EmergencyNumberDto(
    val service: String? = null,
    val number: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChatCategoryDto(
    val id: Long? = null,
    val name: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChatActionDto(
    val type: String? = null,
    val addedProductIds: List<Long>? = null,
    val quantity: Int? = null,
    val cartItemCount: Long? = null,
)

@JsonClass(generateAdapter = true)
data class ChatHistoryResponseDto(
    val conversationId: Long? = null,
    val messages: List<ChatHistoryMessageDto>? = null,
)

@JsonClass(generateAdapter = true)
data class ChatHistoryMessageDto(
    val id: Long? = null,
    val role: String? = null,
    val content: String? = null,
    val intent: String? = null,
    val products: List<ChatProductDto>? = null,
    val alternatives: List<ChatProductDto>? = null,
    val doctorSpecializations: List<String>? = null,
    val emergencyNumbers: List<EmergencyNumberDto>? = null,
    val categories: List<ChatCategoryDto>? = null,
    val createdAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChatProductDto(
    val id: Long? = null,
    val name: String? = null,
    val productName: String? = null,
    val strength: String? = null,
    val packSize: String? = null,
    val form: String? = null,
    val price: Double? = null,
    val scientificName: String? = null,
    val scientificCategory: String? = null,
    val categoryId: Long? = null,
    val consumerCategory: String? = null,
    val company: String? = null,
    val route: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
)
