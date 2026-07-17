package com.medsy.domain.auth.model

data class RegisterParams(
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val role: Role,
    val homeAddress: String?,
    val dob: String,
    val pharmacyId: Long? = null
)
