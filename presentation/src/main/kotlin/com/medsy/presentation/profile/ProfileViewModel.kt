package com.medsy.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.profile.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private val profile = getProfileUseCase()

    private val _state = MutableStateFlow(
        ProfileState(
            name = profile.name,
            image = profile.image,
            phoneNumber = profile.phoneNumber,
        )
    )
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

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

            ProfileUIIntent.SheetDismissed,
            ProfileUIIntent.SheetOptionClicked -> {
                _state.update { it.copy(activeSheet = null) }
            }
        }
    }

    private fun sendEffect(effect: ProfileUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
