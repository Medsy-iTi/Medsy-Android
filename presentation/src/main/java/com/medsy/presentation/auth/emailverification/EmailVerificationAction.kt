package com.medsy.presentation.auth.emailverification

sealed interface EmailVerificationAction {
    data object OnNextClick : EmailVerificationAction
}
