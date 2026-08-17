package com.medsy.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.usecase.RegisterUseCase
import com.medsy.domain.auth.usecase.ValidateEmailUseCase
import com.medsy.domain.auth.usecase.ValidateNameUseCase
import com.medsy.domain.auth.usecase.ValidatePasswordUseCase
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
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
    private val registerUseCase: RegisterUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUIState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterUIIntent) {
        when (intent) {
            is RegisterUIIntent.EmailChanged     -> _state.update { it.copy(email = intent.value, emailErrorRes = null) }
            is RegisterUIIntent.PhoneChanged     -> _state.update { it.copy(phoneNumber = intent.value, phoneErrorRes = null) }
            is RegisterUIIntent.FirstNameChanged -> _state.update { it.copy(firstName = intent.value, firstNameErrorRes = null) }
            is RegisterUIIntent.LastNameChanged  -> _state.update { it.copy(lastName = intent.value, lastNameErrorRes = null) }
            is RegisterUIIntent.PasswordChanged  -> _state.update { it.copy(password = intent.value, passwordErrorRes = null) }
            is RegisterUIIntent.DobChanged       -> _state.update { it.copy(dob = intent.value) }
            is RegisterUIIntent.RoleChanged      -> _state.update { it.copy(role = intent.value) }
            is RegisterUIIntent.AddressChanged   -> _state.update { it.copy(homeAddress = intent.value) }
            is RegisterUIIntent.PharmacyIdChanged -> _state.update { it.copy(pharmacyId = intent.value) }
            RegisterUIIntent.Submit              -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        val s = _state.value

        val firstNameError = when {
            s.firstName.isBlank()               -> R.string.auth_error_required_field
            !validateNameUseCase(s.firstName)   -> R.string.auth_error_invalid_name
            else                                -> null
        }
        val lastNameError = when {
            s.lastName.isBlank()                -> R.string.auth_error_required_field
            !validateNameUseCase(s.lastName)    -> R.string.auth_error_invalid_name
            else                                -> null
        }
        val phoneError = when {
            s.phoneNumber.isBlank()             -> R.string.auth_error_required_field
            else                                -> null // full validation done in UseCase
        }
        val emailError = when {
            s.email.isBlank()                   -> R.string.auth_error_required_field
            !validateEmailUseCase(s.email)      -> R.string.auth_error_invalid_email
            else                                -> null
        }
        val passwordError = when {
            s.password.isBlank()                -> R.string.auth_error_required_field
            s.password.length < 6               -> R.string.auth_error_password_min_6
            s.password.length > 15              -> R.string.auth_error_password_max_15
            s.password.contains(' ')            -> R.string.auth_error_password_spaces
            !s.password.any { it.isLetter() }   -> R.string.auth_error_password_letters_required
            else                                -> null
        }

        if (firstNameError != null || lastNameError != null || phoneError != null ||
            emailError != null || passwordError != null
        ) {
            _state.update {
                it.copy(
                    firstNameErrorRes = firstNameError,
                    lastNameErrorRes  = lastNameError,
                    phoneErrorRes     = phoneError,
                    emailErrorRes     = emailError,
                    passwordErrorRes  = passwordError,
                )
            }
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        val params = RegisterParams(
            email       = s.email,
            phoneNumber = s.phoneNumber,
            firstName   = s.firstName,
            lastName    = s.lastName,
            password    = s.password,
            role        = s.role,
            homeAddress = s.homeAddress.takeIf { it.isNotBlank() },
            dob         = s.dob,
            pharmacyId  = s.pharmacyId,
        )

        when (val result = registerUseCase(params)) {
            is MedsyResult.Success -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(RegisterEffect.NavigateToOtp(s.email))
            }
            is MedsyResult.Error -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(RegisterEffect.ShowError(result.error.toMessageRes()))
            }
        }
    }
}
