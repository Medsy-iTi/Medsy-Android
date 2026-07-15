package com.medsy.presentation.settings

sealed interface SettingsUIIntent {
    data object OnNextClick : SettingsUIIntent
}
