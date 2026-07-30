package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestResultDto(
    val medicineRequestResultItemList: List<RequestResultItemDto>,
    val totalPrice: Double,
)

@JsonClass(generateAdapter = true)
data class RequestResultItemDto(
    val requestItemId: Long,
    val productId: Long,
    val productName: String,
    val imageUrl: String?,
    val unitPrice: Double,
    val alternative: Boolean,
    val available: Boolean,
)
