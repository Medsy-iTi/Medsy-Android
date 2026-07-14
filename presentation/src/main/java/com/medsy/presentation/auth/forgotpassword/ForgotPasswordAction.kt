package com.medsy.presentation.auth.forgotpassword

sealed interface ForgotPasswordAction {
    data object OnNextClick : ForgotPasswordAction
}
