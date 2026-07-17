package com.medsy.domain.common.preferences.model

data class UserPreferences(
    val themeMode: ThemeMode,
    val isOnboardingCompleted: Boolean = false,
)
