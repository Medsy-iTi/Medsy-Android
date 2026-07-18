package com.medsy.data.remote.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
)

@JsonClass(generateAdapter = true)
internal data class MedsyApiErrorResponse(
    val success: Boolean? = null,
    val message: String? = null,
)
