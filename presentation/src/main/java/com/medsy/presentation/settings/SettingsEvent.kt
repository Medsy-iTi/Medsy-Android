package com.medsy.presentation.settings

sealed interface SettingsEvent {
    data object NavigateNext : SettingsEvent
}
