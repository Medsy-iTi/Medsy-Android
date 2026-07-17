package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.AppLanguage
import com.medsy.domain.common.preferences.model.ThemeMode

sealed interface ProfileUIIntent {
    data object RetryProfileLoad : ProfileUIIntent
    data object PersonalDetailsClicked : ProfileUIIntent
    data object LanguageClicked : ProfileUIIntent
    data object AppearanceClicked : ProfileUIIntent
    data object LogoutClicked : ProfileUIIntent
    data object LogoutConfirmed : ProfileUIIntent
    data object SheetDismissed : ProfileUIIntent
    data class LanguageSelected(val language: AppLanguage) : ProfileUIIntent
    data class ThemeSelected(val themeMode: ThemeMode) : ProfileUIIntent
}
