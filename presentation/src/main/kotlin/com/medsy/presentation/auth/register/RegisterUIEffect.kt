package com.medsy.presentation.auth.register

sealed interface RegisterEffect {
    data class NavigateToOtp(val email: String) : RegisterEffect
    data class ShowError(val messageRes: Int) : RegisterEffect
}
