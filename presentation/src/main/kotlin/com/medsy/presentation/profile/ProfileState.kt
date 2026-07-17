package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.ThemeMode

data class ProfileState(
    val name: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val activeSheet: ProfileSheet? = null,
)

enum class ProfileSheet {
    Language,
    Appearance,
    Logout,
}
