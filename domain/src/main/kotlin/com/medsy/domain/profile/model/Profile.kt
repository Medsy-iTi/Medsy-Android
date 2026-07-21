package com.medsy.domain.profile.model

data class Profile(
    val id: Long?,
    val email: String,
    val firstName: String,
    val lastName: String,
    val homeAddress: String?,
    val dob: String?,
    val phoneNumber: String,
    val latitude: Double?,
    val longitude: Double?,
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter(String::isNotBlank)
            .joinToString(separator = " ")

    val hasValidLocation: Boolean
        get() = latitude != null && longitude != null &&
            latitude in -90.0..90.0 && longitude in -180.0..180.0
}
