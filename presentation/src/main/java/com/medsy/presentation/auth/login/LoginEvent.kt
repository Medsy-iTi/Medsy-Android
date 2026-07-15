package com.medsy.presentation.auth.login

sealed interface LoginEvent {
    data object NavigateToSignup : LoginEvent
}
