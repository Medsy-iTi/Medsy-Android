package com.medsy.data.aichat.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CatalogQuestionRequestDto(
    val question: String,
    val lang: String,
    val limit: Int,
)

@JsonClass(generateAdapter = true)
data class CatalogAnswerDto(
    val question: String? = null,
    val answer: String? = null,
    val language: String? = null,
    val semanticSearchUsed: Boolean? = null,
    val sources: List<CatalogSourceDto>? = null,
)

@JsonClass(generateAdapter = true)
data class CatalogSourceDto(
    val product: CatalogProductDto? = null,
    val score: Double? = null,
    val semanticScore: Double? = null,
    val lexicalScore: Double? = null,
    val matchReason: String? = null,
)

@JsonClass(generateAdapter = true)
data class CatalogProductDto(
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
