package com.medsy.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _state.update { it.copy(email = intent.value, errorMessage = null) }
            is LoginIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, errorMessage = null) }
            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = loginUseCase(_state.value.email, _state.value.password)) {
            is DomainResult.Success -> {
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginEffect.NavigateHome)
            }
            is DomainResult.Error -> {
                val message = (result.error as? DomainError.Api)?.message
                    ?: "Something went wrong. Please try again."
                _state.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }
}
