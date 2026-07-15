package com.medsy.presentation.settings

sealed interface SettingsUIEffect {
    data object NavigateNext : SettingsUIEffect
}
