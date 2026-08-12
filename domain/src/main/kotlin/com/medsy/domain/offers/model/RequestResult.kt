package com.medsy.domain.offers.model

data class RequestResult(
    val items: List<RequestResultItem>,
    val totalPrice: Double,
    val paymentMethod: String?,
)

data class RequestResultItem(
    val requestItemId: Long,
    val productId: Long,
    val productName: String,
    val imageUrl: String?,
    val unitPrice: Double,
    val isAlternative: Boolean,
    val isAvailable: Boolean,
)
