package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.ThemeMode

data class ProfileState(
    val name: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessageRes: Int? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val activeSheet: ProfileSheet? = null,
    val isLogoutLoading: Boolean = false,
)

enum class ProfileSheet {
    Language,
    Appearance,
    Logout,
}
