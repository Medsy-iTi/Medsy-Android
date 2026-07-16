package com.medsy.data.remote.auth.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "password") val password: String,
    @Json(name = "role") val role: String,
    @Json(name = "homeAddress") val homeAddress: String?,
    @Json(name = "dob") val dob: String,
    @Json(name = "pharmacyId") val pharmacyId: Long?
)
