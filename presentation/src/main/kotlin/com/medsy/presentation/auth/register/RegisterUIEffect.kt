package com.medsy.presentation.auth.register


sealed interface RegisterUIEffect {
    data object NavigateBack : RegisterUIEffect
    data object NavigateToSignIn : RegisterUIEffect
    data object NavigateToHome : RegisterUIEffect
    data class ShowMessage(val messageRes: Int) : RegisterUIEffect
}