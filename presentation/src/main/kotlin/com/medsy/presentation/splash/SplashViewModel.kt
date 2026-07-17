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

import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase
) : ViewModel() {
    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            // Run session check, user preferences, and minimum splash duration in parallel
            val sessionDeferred = async { observeSessionUseCase().firstOrNull() }
            val preferencesDeferred = async { observeUserPreferencesUseCase().firstOrNull() }
            delay(SplashConstants.MIN_SPLASH_DURATION_MS)
            
            val session = sessionDeferred.await()
            val preferences = preferencesDeferred.await()
            val hasCompletedOnboarding = preferences?.hasCompletedOnboarding == true

            val targetEffect = when {
                session != null -> SplashEffect.ToHome
                hasCompletedOnboarding -> SplashEffect.ToLogin
                else -> SplashEffect.ToOnboarding
            }
            _effect.send(targetEffect)
        }
    }
}

sealed interface SplashEffect {
    data object ToHome : SplashEffect
    data object ToLogin : SplashEffect
    data object ToOnboarding : SplashEffect
}
