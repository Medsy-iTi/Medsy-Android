package com.medsy.presentation.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterUIState())
    val state: StateFlow<RegisterUIState> = _state

    private val _effect = Channel<RegisterUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterUIIntent) {
        when (intent) {
            is RegisterUIIntent.FullNameChanged ->
                _state.update { it.copy(fullName = intent.value, fullNameError = null) }

            is RegisterUIIntent.PhoneNumberChanged ->
                _state.update { it.copy(phoneNumber = intent.value, phoneNumberError = null) }

            is RegisterUIIntent.EmailChanged ->
                _state.update { it.copy(email = intent.value, emailError = null) }

            is RegisterUIIntent.PasswordChanged ->
                _state.update { it.copy(password = intent.value, passwordError = null) }

            is RegisterUIIntent.ConfirmPasswordChanged ->
                _state.update { it.copy(confirmPassword = intent.value, confirmPasswordError = null) }

            RegisterUIIntent.TogglePasswordVisibility ->
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

            RegisterUIIntent.ToggleConfirmPasswordVisibility ->
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }

            is RegisterUIIntent.TermsAcceptedChanged ->
                _state.update { it.copy(isTermsAccepted = intent.accepted, termsError = null) }

            RegisterUIIntent.BackClicked -> sendEffect(RegisterUIEffect.NavigateBack)

            RegisterUIIntent.SignInClicked -> sendEffect(RegisterUIEffect.NavigateToSignIn)

            RegisterUIIntent.SubmitClicked -> submit()
        }
    }

    private fun submit() {
        if (!validate()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            delay(1200)

            _state.update { it.copy(isLoading = false) }
            sendEffect(RegisterUIEffect.ShowMessage(R.string.success_account_created))
            sendEffect(RegisterUIEffect.NavigateToHome)
        }
    }

    private fun validate(): Boolean {
        val current = _state.value

        val fullNameError = if (current.fullName.isBlank()) R.string.error_name_required else null
        val phoneError = if (!isValidPhone(current.phoneNumber)) R.string.error_phone_invalid else null
        val emailError = if (!isValidEmail(current.email)) R.string.error_email_invalid else null
        val passwordError = if (current.password.length < 8) R.string.error_password_too_short else null
        val confirmError = if (current.password != current.confirmPassword) {
            R.string.error_password_mismatch
        } else null
        val termsError = if (!current.isTermsAccepted) R.string.error_terms_not_accepted else null

        _state.update {
            it.copy(
                fullNameError = fullNameError,
                phoneNumberError = phoneError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmError,
                termsError = termsError,
            )
        }

        return listOf(fullNameError, phoneError, emailError, passwordError, confirmError, termsError)
            .all { it == null }
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPhone(phone: String): Boolean =
        phone.length in 8..15 && phone.all { it.isDigit() || it == '+' }

    private fun sendEffect(effect: RegisterUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private inline fun MutableStateFlow<RegisterUIState>.update(
        block: (RegisterUIState) -> RegisterUIState,
    ) {
        value = block(value)
    }
}