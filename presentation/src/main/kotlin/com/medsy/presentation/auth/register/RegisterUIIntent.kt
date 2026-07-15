package com.medsy.presentation.auth.register

sealed interface RegisterUIIntent {
    data class FullNameChanged(val value: String) : RegisterUIIntent
    data class PhoneNumberChanged(val value: String) : RegisterUIIntent
    data class EmailChanged(val value: String) : RegisterUIIntent
    data class PasswordChanged(val value: String) : RegisterUIIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterUIIntent
    data object TogglePasswordVisibility : RegisterUIIntent
    data object ToggleConfirmPasswordVisibility : RegisterUIIntent
    data class TermsAcceptedChanged(val accepted: Boolean) : RegisterUIIntent
    data object SubmitClicked : RegisterUIIntent
    data object BackClicked : RegisterUIIntent
    data object SignInClicked : RegisterUIIntent
}