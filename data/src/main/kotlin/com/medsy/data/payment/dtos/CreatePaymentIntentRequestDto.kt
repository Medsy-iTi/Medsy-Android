package com.medsy.data.payment.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePaymentIntentRequestDto(
    @Json(name = "orderId")
    val orderId: Long,
)