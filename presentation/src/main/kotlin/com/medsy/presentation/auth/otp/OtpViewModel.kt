package com.medsy.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.VerifyOtpUseCase
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val _effect = Channel<OtpEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        startTimer()
    }

    fun setEmail(email: String) {
        if (_state.value.email.isEmpty() && email.isNotEmpty()) {
            _state.update { it.copy(email = email) }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            _state.update { it.copy(countdown = 300) }
            while (_state.value.countdown > 0) {
                delay(1000)
                _state.update { it.copy(countdown = it.countdown - 1) }
            }
        }
    }

    fun onIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.CodeChanged -> _state.update { it.copy(code = intent.value, errorMessage = null) }
            OtpIntent.Submit -> submit()
            OtpIntent.Resend -> {
                // TODO: Integrate resend API when available from backend
                startTimer()
            }
            OtpIntent.Tick -> { /* handled by loop */ }
        }
    }

    private fun submit() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = verifyOtpUseCase(_state.value.email, _state.value.code)) {
                is DomainResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(OtpEffect.NavigateHome)
                }
                is DomainResult.Error -> {
                    val message = (result.error as? DomainError.Api)?.message
                        ?: "Invalid code."
                    _state.update { it.copy(isLoading = false, errorMessage = message) }
                }
            }
        }
    }
}
