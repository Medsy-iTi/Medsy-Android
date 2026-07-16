package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.ThemeMode

data class ProfileState(
    val name: String = "",
    val image: String? = null,
    val phoneNumber: String = "",
    val isVerified: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System,
    val activeSheet: ProfileSheet? = null,
)

enum class ProfileSheet {
    Language,
    Appearance,
}
