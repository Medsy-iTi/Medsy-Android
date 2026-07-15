package com.medsy.presentation.auth.login

sealed interface LoginUIIntent {
    data object OnSignupClick : LoginUIIntent
    data object OnLoginClick : LoginUIIntent
}
