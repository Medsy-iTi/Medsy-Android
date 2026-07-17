package com.medsy.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase
) : ViewModel() {
    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            // Run session check and minimum splash duration in parallel,
            // then navigate only after both complete.
            val sessionDeferred = async { observeSessionUseCase().firstOrNull() }
            delay(SplashConstants.MIN_SPLASH_DURATION_MS)
            val session = sessionDeferred.await()
            _effect.send(if (session != null) SplashEffect.ToHome else SplashEffect.ToOnboarding)
        }
    }
}

sealed interface SplashEffect {
    data object ToHome : SplashEffect
    data object ToOnboarding : SplashEffect
}
