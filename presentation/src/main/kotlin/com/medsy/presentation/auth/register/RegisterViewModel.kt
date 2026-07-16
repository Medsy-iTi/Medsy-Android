package com.medsy.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.usecase.RegisterUseCase
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
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
            is RegisterIntent.EmailChanged -> _state.update { it.copy(email = intent.value, errorMessage = null) }
            is RegisterIntent.PhoneChanged -> _state.update { it.copy(phoneNumber = intent.value, errorMessage = null) }
            is RegisterIntent.FirstNameChanged -> _state.update { it.copy(firstName = intent.value, errorMessage = null) }
            is RegisterIntent.LastNameChanged -> _state.update { it.copy(lastName = intent.value, errorMessage = null) }
            is RegisterIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, errorMessage = null) }
            is RegisterIntent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = intent.value, errorMessage = null) }
            is RegisterIntent.DobChanged -> _state.update { it.copy(dob = intent.value, errorMessage = null) }
            is RegisterIntent.RoleChanged -> _state.update { it.copy(role = intent.value, errorMessage = null) }
            is RegisterIntent.AddressChanged -> _state.update { it.copy(homeAddress = intent.value, errorMessage = null) }
            is RegisterIntent.PharmacyIdChanged -> _state.update { it.copy(pharmacyId = intent.value, errorMessage = null) }
            RegisterIntent.Submit -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        val currentState = _state.value
        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match") }
            return@launch
        }
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val params = RegisterParams(
            email = currentState.email,
            phoneNumber = currentState.phoneNumber,
            firstName = currentState.firstName,
            lastName = currentState.lastName,
            password = currentState.password,
            role = currentState.role,
            homeAddress = currentState.homeAddress.takeIf { it.isNotBlank() },
            dob = currentState.dob,
            pharmacyId = currentState.pharmacyId
        )
        
        when (val result = registerUseCase(params)) {
            is DomainResult.Success -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(RegisterEffect.NavigateToOtp(currentState.email))
            }
            is DomainResult.Error -> {
                val message = (result.error as? DomainError.Api)?.message
                    ?: "Something went wrong. Please try again."
                _state.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }
}