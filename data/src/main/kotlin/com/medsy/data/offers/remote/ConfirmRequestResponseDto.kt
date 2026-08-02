package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfirmRequestResponseDto(
    val requestId: Long,
    val orders: List<ConfirmOrderDto>
)

@JsonClass(generateAdapter = true)
data class ConfirmOrderDto(
    val orderId: Long,
    val pharmacyId: Long,
    val pharmacyName: String?,
    val itemIds: List<Long>
)
