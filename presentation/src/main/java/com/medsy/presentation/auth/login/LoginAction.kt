package com.medsy.presentation.auth.login

sealed interface LoginAction {
    data object OnSignupClick : LoginAction
}
