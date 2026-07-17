package com.medsy.domain.auth.model

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val homeAddress: String?,
    val dob: String?
)

enum class Role { CUSTOMER, PHARMACIST, ADMIN }
