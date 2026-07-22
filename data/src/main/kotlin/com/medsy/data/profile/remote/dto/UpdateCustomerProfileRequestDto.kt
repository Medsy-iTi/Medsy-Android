package com.medsy.data.profile.remote.dto

data class UpdateCustomerProfileRequestDto(
    val firstName: String,
    val lastName: String,
    val homeAddress: String?,
    val dob: String?,
    val latitude: Double?,
    val longitude: Double?,
)
