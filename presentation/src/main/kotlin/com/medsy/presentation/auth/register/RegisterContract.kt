package com.medsy.presentation.auth.register

import com.medsy.domain.auth.model.Role

data class RegisterState(
    val email: String = "",
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val dob: String = "",
    val role: Role = Role.CUSTOMER,
    val homeAddress: String = "",
    val pharmacyId: Long? = null,
    val isLoading: Boolean = false,
    val emailErrorRes: Int? = null,
    val phoneErrorRes: Int? = null,
    val firstNameErrorRes: Int? = null,
    val lastNameErrorRes: Int? = null,
    val passwordErrorRes: Int? = null,
)

sealed interface RegisterIntent {
    data class EmailChanged(val value: String) : RegisterIntent
    data class PhoneChanged(val value: String) : RegisterIntent
    data class FirstNameChanged(val value: String) : RegisterIntent
    data class LastNameChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class DobChanged(val value: String) : RegisterIntent
    data class RoleChanged(val value: Role) : RegisterIntent
    data class AddressChanged(val value: String) : RegisterIntent
    data class PharmacyIdChanged(val value: Long?) : RegisterIntent
    data object Submit : RegisterIntent
}

sealed interface RegisterEffect {
    data class NavigateToOtp(val email: String) : RegisterEffect
    data class ShowError(val messageRes: Int) : RegisterEffect
}
