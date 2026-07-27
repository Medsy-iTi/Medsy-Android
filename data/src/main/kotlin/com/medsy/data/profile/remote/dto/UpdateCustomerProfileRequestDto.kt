package com.medsy.data.profile.remote.dto

data class UpdateCustomerProfileRequestDto(
    val firstName: String,
    val lastName: String,
    val homeAddress: String?,
    val dob: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
)
