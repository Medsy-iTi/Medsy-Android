package com.medsy.presentation.profile

data class ProfileState(
    val name: String = "",
    val image: String? = null,
    val phoneNumber: String = "",
    val isVerified: Boolean = false,
    val activeSheet: ProfileSheet? = null,
)

enum class ProfileSheet {
    Language,
    Appearance,
}
