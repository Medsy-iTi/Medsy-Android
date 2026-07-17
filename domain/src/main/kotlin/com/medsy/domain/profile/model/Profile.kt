package com.medsy.domain.profile.model

data class Profile(
    val id: Long?,
    val email: String,
    val firstName: String,
    val lastName: String,
    val homeAddress: String?,
    val dob: String?,
    val phoneNumber: String,
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter(String::isNotBlank)
            .joinToString(separator = " ")
}
