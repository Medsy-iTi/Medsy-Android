package com.medsy.data.payment.dtos


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePaymentIntentResponseDto(
    @Json(name = "paymentIntentId")
    val paymentIntentId: String,

    @Json(name = "clientSecret")
    val clientSecret: String,
)