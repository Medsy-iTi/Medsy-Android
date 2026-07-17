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

sealed interface RegisterUIIntent {
    data class EmailChanged(val value: String) : RegisterUIIntent
    data class PhoneChanged(val value: String) : RegisterUIIntent
    data class FirstNameChanged(val value: String) : RegisterUIIntent
    data class LastNameChanged(val value: String) : RegisterUIIntent
    data class PasswordChanged(val value: String) : RegisterUIIntent
    data class DobChanged(val value: String) : RegisterUIIntent
    data class RoleChanged(val value: Role) : RegisterUIIntent
    data class AddressChanged(val value: String) : RegisterUIIntent
    data class PharmacyIdChanged(val value: Long?) : RegisterUIIntent
    data object Submit : RegisterUIIntent
}

sealed interface RegisterEffect {
    data class NavigateToOtp(val email: String) : RegisterEffect
    data class ShowError(val messageRes: Int) : RegisterEffect
}
