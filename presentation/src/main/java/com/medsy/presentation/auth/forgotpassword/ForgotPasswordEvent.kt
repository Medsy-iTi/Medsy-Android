package com.medsy.presentation.auth.forgotpassword

sealed interface ForgotPasswordEvent {
    data object NavigateNext : ForgotPasswordEvent
}
