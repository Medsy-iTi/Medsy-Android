package com.medsy.presentation.profile

sealed interface ProfileEvent {
    data object NavigateNext : ProfileEvent
}
