package com.medsy.presentation.settings

sealed interface SettingsAction {
    data object OnNextClick : SettingsAction
}
