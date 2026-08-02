package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OfferDto(
    val id: Long,
    val requestId: Long,
    val pharmacyId: Long,
    val pharmacistId: Long,
    val pharmacyName: String?,
    val pharmacistName: String?,
    val status: String,
    val distanceKm: Double?,
    val items: List<OfferItemDto>
)

@JsonClass(generateAdapter = true)
data class OfferItemDto(
    val id: Long,
    val requestItemId: Long,
    val productId: Int
)
