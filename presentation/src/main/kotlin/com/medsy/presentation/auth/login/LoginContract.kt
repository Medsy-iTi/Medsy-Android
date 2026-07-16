package com.medsy.presentation.auth.login

import com.medsy.designsystem.components.MedsySnackbarData

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailErrorRes: Int? = null,
    val passwordErrorRes: Int? = null,
    val snackbar: MedsySnackbarData? = null
)

sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object DismissSnackbar : LoginIntent
    data object Submit : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
}
