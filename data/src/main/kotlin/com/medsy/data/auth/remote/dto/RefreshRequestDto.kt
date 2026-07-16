package com.medsy.data.auth.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshRequestDto(
    @Json(name = "refreshToken") val refreshToken: String
)
