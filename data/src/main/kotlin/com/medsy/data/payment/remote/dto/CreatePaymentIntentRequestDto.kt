package com.medsy.data.payment.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePaymentIntentRequestDto(
    @Json(name = "orderId")
    val masterOrderId: Long,
)