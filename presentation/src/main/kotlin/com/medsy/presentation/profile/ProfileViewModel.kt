package com.medsy.presentation.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.model.AppLanguage
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.common.preferences.usecase.SetThemeModeUseCase
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.profile.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
        observeUserPreferences()
    }

    private fun loadProfile() {
        val profile = getProfileUseCase()

        _state.update { state ->
            state.copy(
                name = profile.name,
                image = profile.image,
                phoneNumber = profile.phoneNumber,
            )
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            observeUserPreferencesUseCase().collect { preferences ->
                _state.update {
                    it.copy(themeMode = preferences.themeMode)
                }
            }
        }
    }

    private fun changeLanguage(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.tag)
        )
    }

    private fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(themeMode)
        }
    }

    private fun logout() {
        _state.update { it.copy(isLogoutLoading = true) }
        viewModelScope.launch {
            android.util.Log.d("ProfileViewModel", "Starting API logout request...")
            logoutUseCase()
            android.util.Log.d("ProfileViewModel", "API logout finished. Session cleared locally.")
            _state.update { it.copy(isLogoutLoading = false, activeSheet = null) }
            _effect.send(ProfileUIEffect.NavigateToLogin)
        }
    }

    fun onIntent(intent: ProfileUIIntent) {
        when (intent) {
            ProfileUIIntent.PersonalDetailsClicked -> {
                sendEffect(ProfileUIEffect.NavigateToPersonalDetails)
            }

            ProfileUIIntent.LanguageClicked -> {
                _state.update { it.copy(activeSheet = ProfileSheet.Language) }
            }

            ProfileUIIntent.AppearanceClicked -> {
                _state.update { it.copy(activeSheet = ProfileSheet.Appearance) }
            }

            ProfileUIIntent.LogoutClicked -> {
                _state.update { it.copy(activeSheet = ProfileSheet.Logout) }
            }

            ProfileUIIntent.LogoutConfirmed -> {
                logout()
            }

            ProfileUIIntent.SheetDismissed -> {
                _state.update { it.copy(activeSheet = null) }
            }

            is ProfileUIIntent.LanguageSelected -> {
                _state.update { it.copy(activeSheet = null) }
                changeLanguage(intent.language)
            }

            is ProfileUIIntent.ThemeSelected -> {
                _state.update { it.copy(activeSheet = null) }
                setTheme(intent.themeMode)
            }
        }
    }

    private fun sendEffect(effect: ProfileUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
