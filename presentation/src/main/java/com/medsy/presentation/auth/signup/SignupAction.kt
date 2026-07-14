package com.medsy.presentation.auth.signup

sealed interface SignupAction {
    data object OnNextClick : SignupAction
}
