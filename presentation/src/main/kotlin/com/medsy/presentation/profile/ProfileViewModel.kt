package com.medsy.presentation.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.usecase.ClearCartDraftUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.common.preferences.model.AppLanguage
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.common.preferences.usecase.SetThemeModeUseCase
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.profile.usecase.LogoutUseCase
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val clearCartDraftUseCase: ClearCartDraftUseCase
) : ViewModel() {

    private var hasCompletedInitialLoad = false

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState(),
        )

    private val _effect = Channel<ProfileUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeUserPreferences()
    }

    private suspend fun loadProfile() {
        _state.update {
            it.copy(
                isLoading = true,
                hasError = false,
                errorMessageRes = null,
            )
        }
        getProfileUseCase()
            .onSuccess { profile ->
                _state.update { state ->
                    state.copy(
                        name = profile.fullName,
                        phoneNumber = profile.phoneNumber,
                        hasSavedLocation = profile.hasValidLocation,
                        isLoading = false,
                    )
                }
                hasCompletedInitialLoad = true
            }
            .onError { error ->
                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessageRes = error.toMessageRes(),
                    )
                }
                hasCompletedInitialLoad = true
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
            clearCartDraftUseCase()
            logoutUseCase()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLogoutLoading = false,
                            activeSheet = null,
                            errorMessageRes = null,
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLogoutLoading = false,
                            activeSheet = null,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }

            _effect.send(ProfileUIEffect.NavigateToLogin)
        }
    }

    fun onIntent(intent: ProfileUIIntent) {
        when (intent) {
            ProfileUIIntent.ScreenResumed -> {
                if (hasCompletedInitialLoad) {
                    viewModelScope.launch { loadProfile() }
                }
            }

            ProfileUIIntent.RetryProfileLoad -> {
                viewModelScope.launch { loadProfile() }
            }

            ProfileUIIntent.PersonalDetailsClicked -> {
                sendEffect(
                    ProfileUIEffect.NavigateToPersonalDetails(startInEditMode = false)
                )
            }

            ProfileUIIntent.AddAddressClicked -> {
                sendEffect(
                    ProfileUIEffect.NavigateToPersonalDetails(startInEditMode = true)
                )
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
