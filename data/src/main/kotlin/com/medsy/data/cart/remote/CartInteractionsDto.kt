package com.medsy.data.cart.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartInteractionsDto(
    val warnings: List<InteractionWarningDto>? = null,
)

@JsonClass(generateAdapter = true)
data class InteractionWarningDto(
    val severity: String? = null,
    val title: String? = null,
    val advice: String? = null,
    val involvedProducts: List<InvolvedProductDto>? = null,
)

@JsonClass(generateAdapter = true)
data class InvolvedProductDto(
    val productId: Long? = null,
    val productName: String? = null,
    val ingredient: String? = null,
)
