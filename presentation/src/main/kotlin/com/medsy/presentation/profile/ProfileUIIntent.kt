package com.medsy.presentation.profile

import AppLanguage
import com.medsy.domain.common.preferences.model.ThemeMode

sealed interface ProfileUIIntent {
    data object ScreenResumed : ProfileUIIntent
    data object RetryProfileLoad : ProfileUIIntent
    data object PersonalDetailsClicked : ProfileUIIntent
    data object FavoritesClicked : ProfileUIIntent
    data object RemindersClicked : ProfileUIIntent
    data object AddAddressClicked : ProfileUIIntent
    data object LanguageClicked : ProfileUIIntent
    data object AppearanceClicked : ProfileUIIntent
    data object LogoutClicked : ProfileUIIntent
    data object LogoutConfirmed : ProfileUIIntent
    data object SheetDismissed : ProfileUIIntent
    data class LanguageSelected(val language: AppLanguage) : ProfileUIIntent
    data class ThemeSelected(val themeMode: ThemeMode) : ProfileUIIntent
}
