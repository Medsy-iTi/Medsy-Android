package com.medsy.presentation.profile.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.profile.model.UpdateProfileParams
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

    private var startEditingAddressRequested = false
    private var hasHandledStartEditingAddress = false

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

            PersonalDetailsUIIntent.StartEditingAddress -> {
                if (hasHandledStartEditingAddress) return
                hasHandledStartEditingAddress = true
                startEditingAddressRequested = true
                _state.update { state ->
                    state.copy(isEditing = state.profile != null || state.isEditing)
                }
            }

            PersonalDetailsUIIntent.CancelEditClicked -> {
                startEditingAddressRequested = false
                _state.update { state ->
                    state.copy(
                        isEditing = false,
                        isMapPickerVisible = false,
                        draftFirstName = state.profile?.firstName.orEmpty(),
                        draftLastName = state.profile?.lastName.orEmpty(),
                        draftHomeAddress = state.profile?.homeAddress.orEmpty(),
                        draftDob = state.profile?.dob.orEmpty(),
                        draftLatitude = state.profile?.latitude,
                        draftLongitude = state.profile?.longitude,
                    )
                }
            }

            is PersonalDetailsUIIntent.FirstNameChanged -> {
                _state.update { it.copy(draftFirstName = intent.value) }
            }

            is PersonalDetailsUIIntent.LastNameChanged -> {
                _state.update { it.copy(draftLastName = intent.value) }
            }

            is PersonalDetailsUIIntent.HomeAddressChanged -> {
                _state.update { it.copy(draftHomeAddress = intent.value) }
            }

            is PersonalDetailsUIIntent.DobSelected -> {
                _state.update { it.copy(draftDob = intent.value) }
            }

            PersonalDetailsUIIntent.LocationPickerClicked -> {
                _state.update { it.copy(isMapPickerVisible = true) }
            }

            PersonalDetailsUIIntent.LocationPickerDismissed -> {
                _state.update { it.copy(isMapPickerVisible = false) }
            }

            is PersonalDetailsUIIntent.LocationSelected -> {
                _state.update {
                    it.copy(
                        isMapPickerVisible = false,
                        draftLatitude = intent.latitude,
                        draftLongitude = intent.longitude,
                    )
                }
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
                isEditing = startEditingAddressRequested,
                isMapPickerVisible = false,
            )
        }
        getProfileUseCase()
            .onSuccess { profile ->
                _state.update {
                    it.copy(
                        profile = profile,
                        isLoading = false,
                        isEditing = startEditingAddressRequested,
                        draftFirstName = profile.firstName,
                        draftLastName = profile.lastName,
                        draftHomeAddress = profile.homeAddress.orEmpty(),
                        draftDob = profile.dob.orEmpty(),
                        draftLatitude = profile.latitude,
                        draftLongitude = profile.longitude,
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
        if (!currentState.isEditing || currentState.isSaving || !currentState.hasChanges ||
            !currentState.isFirstNameValid || !currentState.isLastNameValid
        ) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            updateProfileUseCase(
                params = UpdateProfileParams(
                    firstName = currentState.draftFirstName,
                    lastName = currentState.draftLastName,
                    homeAddress = currentState.draftHomeAddress
                        .takeIf { it != profile.homeAddress.orEmpty() }
                        ?: profile.homeAddress,
                    dob = currentState.draftDob
                        .takeIf { it != profile.dob.orEmpty() }
                        ?: profile.dob,
                    latitude = currentState.draftLatitude,
                    longitude = currentState.draftLongitude,
                ),
            ).onSuccess { updatedProfile ->
                startEditingAddressRequested = false
                _state.update {
                    it.copy(
                        profile = updatedProfile,
                        isEditing = false,
                        isSaving = false,
                        draftFirstName = updatedProfile.firstName,
                        draftLastName = updatedProfile.lastName,
                        draftHomeAddress = updatedProfile.homeAddress.orEmpty(),
                        draftDob = updatedProfile.dob.orEmpty(),
                        draftLatitude = updatedProfile.latitude,
                        draftLongitude = updatedProfile.longitude,
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
