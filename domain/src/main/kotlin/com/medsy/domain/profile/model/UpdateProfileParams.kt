package com.medsy.domain.profile.model

data class UpdateProfileParams(
    val firstName: String,
    val lastName: String,
    val homeAddress: String?,
    val dob: String?,
    val latitude: Double?,
    val longitude: Double?,
)
