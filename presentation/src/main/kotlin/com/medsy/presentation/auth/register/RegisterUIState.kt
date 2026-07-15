package com.medsy.presentation.auth.register
data class RegisterUIState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isTermsAccepted: Boolean = false,
    val isLoading: Boolean = false,

    val fullNameError: Int? = null,
    val phoneNumberError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val termsError: Int? = null,
) {
    val isFormValid: Boolean
        get() = fullName.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                isTermsAccepted
}
