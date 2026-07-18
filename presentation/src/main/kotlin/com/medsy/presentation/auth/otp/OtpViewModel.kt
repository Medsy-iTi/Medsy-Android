package com.medsy.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.VerifyOtpUseCase
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
                delay(1000.milliseconds)
                _state.update { it.copy(countdown = it.countdown - 1) }
            }
        }
    }

    fun onIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.CodeChanged -> _state.update {
                it.copy(
                    code = intent.value,
                    hasError = false
                )
            }

            OtpIntent.Submit -> submit()
            OtpIntent.Resend -> {
                startTimer()
            }

            OtpIntent.Tick -> { /* handled by loop */
            }
        }
    }

    private fun submit() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasError = false) }
            when (val result = verifyOtpUseCase(_state.value.email, _state.value.code)) {
                is MedsyResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(OtpEffect.NavigateHome)
                }

                is MedsyResult.Error -> {
                    _state.update { it.copy(isLoading = false, hasError = true) }
                    _effect.send(OtpEffect.ShowError(result.error.toMessageRes()))
                }
            }
        }
    }
}
