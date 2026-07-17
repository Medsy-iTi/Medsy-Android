package com.medsy.data.profile.remote.dto

data class CustomerDto(
    val id: Long?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val homeAddress: String?,
    val dob: String?,
    val phoneNumber: String?,
)
