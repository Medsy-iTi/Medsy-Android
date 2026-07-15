package com.medsy.presentation.auth.signup

sealed interface SignupEvent {
    data object NavigateNext : SignupEvent
}
