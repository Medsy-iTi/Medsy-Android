package com.medsy.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.designsystem.components.MedsySnackbarData
import com.medsy.designsystem.components.MedsySnackbarType
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.usecase.RegisterUseCase
import com.medsy.domain.common.DomainResult
import com.medsy.presentation.common.util.toSnackbarData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.EmailChanged -> _state.update { it.copy(email = intent.value, emailErrorRes = null, snackbar = null) }
            is RegisterIntent.PhoneChanged -> _state.update { it.copy(phoneNumber = intent.value, phoneErrorRes = null, snackbar = null) }
            is RegisterIntent.FirstNameChanged -> _state.update { it.copy(firstName = intent.value, firstNameErrorRes = null, snackbar = null) }
            is RegisterIntent.LastNameChanged -> _state.update { it.copy(lastName = intent.value, lastNameErrorRes = null, snackbar = null) }
            is RegisterIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, passwordErrorRes = null, snackbar = null) }
            is RegisterIntent.DobChanged -> _state.update { it.copy(dob = intent.value, snackbar = null) }
            is RegisterIntent.RoleChanged -> _state.update { it.copy(role = intent.value, snackbar = null) }
            is RegisterIntent.AddressChanged -> _state.update { it.copy(homeAddress = intent.value, snackbar = null) }
            is RegisterIntent.PharmacyIdChanged -> _state.update { it.copy(pharmacyId = intent.value, snackbar = null) }
            RegisterIntent.DismissSnackbar -> _state.update { it.copy(snackbar = null) }
            RegisterIntent.Submit -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        val s = _state.value
        val emailError = if (s.email.isBlank()) com.medsy.presentation.R.string.auth_error_required_field else null
        val phoneError = if (s.phoneNumber.isBlank()) com.medsy.presentation.R.string.auth_error_required_field else null
        val firstNameError = if (s.firstName.isBlank()) com.medsy.presentation.R.string.auth_error_required_field else null
        val lastNameError = if (s.lastName.isBlank()) com.medsy.presentation.R.string.auth_error_required_field else null
        val passwordError = when {
            s.password.isBlank() -> com.medsy.presentation.R.string.auth_error_required_field
            s.password.length < 6 -> com.medsy.presentation.R.string.auth_error_password_min_6
            else -> null
        }

        if (emailError != null || phoneError != null || firstNameError != null || lastNameError != null || passwordError != null) {
            _state.update {
                it.copy(
                    emailErrorRes = emailError,
                    phoneErrorRes = phoneError,
                    firstNameErrorRes = firstNameError,
                    lastNameErrorRes = lastNameError,
                    passwordErrorRes = passwordError
                )
            }
            return@launch
        }

        _state.update { it.copy(isLoading = true, snackbar = null) }

        val params = RegisterParams(
            email = s.email,
            phoneNumber = s.phoneNumber,
            firstName = s.firstName,
            lastName = s.lastName,
            password = s.password,
            role = s.role,
            homeAddress = s.homeAddress.takeIf { it.isNotBlank() },
            dob = s.dob,
            pharmacyId = s.pharmacyId
        )

        when (val result = registerUseCase(params)) {
            is DomainResult.Success -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(RegisterEffect.NavigateToOtp(s.email))
            }
            is DomainResult.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        snackbar = result.error.toSnackbarData()
                    )
                }
            }
        }
    }
}