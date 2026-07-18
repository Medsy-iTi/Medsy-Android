package com.medsy.presentation.profile.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.profile.usecase.UpdateProfileUseCase
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
class PersonalDetailsViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PersonalDetailsState())
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PersonalDetailsState(),
        )

    private val _effect = Channel<PersonalDetailsUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: PersonalDetailsUIIntent) {
        when (intent) {
            PersonalDetailsUIIntent.Retry -> {
                viewModelScope.launch { loadProfile() }
            }

            PersonalDetailsUIIntent.EditClicked -> {
                _state.update { it.copy(isEditing = true) }
            }

            PersonalDetailsUIIntent.CancelEditClicked -> {
                _state.update { state ->
                    state.copy(
                        isEditing = false,
                        draftHomeAddress = state.profile?.homeAddress.orEmpty(),
                        draftDob = state.profile?.dob.orEmpty(),
                    )
                }
            }

            is PersonalDetailsUIIntent.HomeAddressChanged -> {
                _state.update { it.copy(draftHomeAddress = intent.value) }
            }

            is PersonalDetailsUIIntent.DobSelected -> {
                _state.update { it.copy(draftDob = intent.value) }
            }

            PersonalDetailsUIIntent.SaveClicked -> saveChanges()
        }
    }

    private suspend fun loadProfile() {
        _state.update {
            it.copy(
                isLoading = true,
                hasLoadError = false,
                loadErrorMessageRes = null,
                isEditing = false,
            )
        }
        getProfileUseCase()
            .onSuccess { profile ->
                _state.update {
                    it.copy(
                        profile = profile,
                        isLoading = false,
                        draftHomeAddress = profile.homeAddress.orEmpty(),
                        draftDob = profile.dob.orEmpty(),
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasLoadError = true,
                        loadErrorMessageRes = error.toMessageRes(),
                    )
                }
            }
    }

    private fun saveChanges() {
        val currentState = _state.value
        val profile = currentState.profile ?: return
        if (!currentState.isEditing || currentState.isSaving || !currentState.hasChanges) return

        val updatedAddress = currentState.draftHomeAddress
            .takeIf { it != profile.homeAddress.orEmpty() }
        val updatedDob = currentState.draftDob
            .takeIf { it != profile.dob.orEmpty() }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            updateProfileUseCase(
                homeAddress = updatedAddress,
                dob = updatedDob,
            ).onSuccess { updatedProfile ->
                _state.update {
                    it.copy(
                        profile = updatedProfile,
                        isEditing = false,
                        isSaving = false,
                        draftHomeAddress = updatedProfile.homeAddress.orEmpty(),
                        draftDob = updatedProfile.dob.orEmpty(),
                    )
                }
                _effect.send(PersonalDetailsUIEffect.SaveSucceeded)
            }.onError { error ->
                _state.update { it.copy(isSaving = false) }
                _effect.send(PersonalDetailsUIEffect.SaveFailed(error.toMessageRes()))
            }
        }
    }
}
