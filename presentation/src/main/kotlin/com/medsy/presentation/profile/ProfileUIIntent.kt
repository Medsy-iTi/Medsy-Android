package com.medsy.presentation.profile

sealed interface ProfileUIIntent {
    data object PersonalDetailsClicked : ProfileUIIntent
    data object LanguageClicked : ProfileUIIntent
    data object AppearanceClicked : ProfileUIIntent
    data object SheetDismissed : ProfileUIIntent
    data object SheetOptionClicked : ProfileUIIntent
}
