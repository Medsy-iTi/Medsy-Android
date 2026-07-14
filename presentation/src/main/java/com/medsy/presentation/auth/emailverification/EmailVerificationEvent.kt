package com.medsy.presentation.auth.emailverification

sealed interface EmailVerificationEvent {
    data object NavigateNext : EmailVerificationEvent
}
