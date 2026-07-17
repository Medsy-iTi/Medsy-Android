package com.medsy.domain.common.preferences.model

data class UserPreferences(
    val themeMode: ThemeMode,
    val hasCompletedOnboarding: Boolean = false,
)
