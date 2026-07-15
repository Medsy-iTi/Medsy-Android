package com.medsy.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface LoginUIEffect {
    data object NavigateToSignup : LoginUIEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginUIEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginUIIntent) {
        when (intent) {
            LoginUIIntent.OnSignupClick -> {
                viewModelScope.launch {
                    _effect.send(LoginUIEffect.NavigateToSignup)
                }
            }
        }
    }
}
