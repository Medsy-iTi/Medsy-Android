package com.medsy.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.designsystem.components.MedsySnackbarData
import com.medsy.designsystem.components.MedsySnackbarType
import com.medsy.domain.auth.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _state.update { it.copy(email = intent.value, emailErrorRes = null, snackbar = null) }
            is LoginIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, passwordErrorRes = null, snackbar = null) }
            LoginIntent.DismissSnackbar -> _state.update { it.copy(snackbar = null) }
            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        val emailError = if (_state.value.email.isBlank()) com.medsy.presentation.R.string.auth_error_required_field else null
        val passwordError = when {
            _state.value.password.isBlank() -> com.medsy.presentation.R.string.auth_error_required_field
            _state.value.password.length < 6 -> com.medsy.presentation.R.string.auth_error_password_min_6
            else -> null
        }

        if (emailError != null || passwordError != null) {
            _state.update { it.copy(emailErrorRes = emailError, passwordErrorRes = passwordError) }
            return@launch
        }

        _state.update { it.copy(isLoading = true, snackbar = null, emailErrorRes = null, passwordErrorRes = null) }

        when (val result = loginUseCase(_state.value.email, _state.value.password)) {
            is DomainResult.Success -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginEffect.NavigateHome)
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
