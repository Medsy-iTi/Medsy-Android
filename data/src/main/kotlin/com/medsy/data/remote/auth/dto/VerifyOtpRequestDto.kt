package com.medsy.data.remote.auth.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "otpCode") val otpCode: String
)
