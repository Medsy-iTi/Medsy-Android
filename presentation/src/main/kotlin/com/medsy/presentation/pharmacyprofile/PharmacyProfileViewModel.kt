package com.medsy.presentation.pharmacyprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.pharmacyprofile.usecase.GetPharmacyByIdUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PharmacyProfileViewModel @Inject constructor(
    private val getPharmacyByIdUseCase: GetPharmacyByIdUseCase,
) : ViewModel() {

    private var pharmacyId: Long = DEFAULT_PHARMACY_ID
    private var initializedPharmacyId: Long? = null

    private val _state = MutableStateFlow(PharmacyProfileState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = PharmacyProfileState(),
    )

    private val _effect = Channel<PharmacyProfileUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun init(pharmacyId: Long) {
        if (initializedPharmacyId == pharmacyId) return
        initializedPharmacyId = pharmacyId
        this.pharmacyId = pharmacyId
        loadPharmacy()
    }

    fun onIntent(intent: PharmacyProfileUIIntent) {
        when (intent) {
            PharmacyProfileUIIntent.BackClicked -> sendEffect(
                PharmacyProfileUIEffect.NavigateBack
            )

            PharmacyProfileUIIntent.RetryClicked -> loadPharmacy()
            PharmacyProfileUIIntent.CallClicked -> callPharmacy()
            PharmacyProfileUIIntent.DirectionsClicked -> openDirections()
        }
    }

    private fun loadPharmacy() {
        if (pharmacyId <= 0L) {
            _state.update {
                it.copy(
                    isLoading = false,
                    pharmacy = null,
                    errorMessageRes = R.string.pharmacy_profile_invalid_id,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
            getPharmacyByIdUseCase(pharmacyId)
                .onSuccess { pharmacy ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pharmacy = pharmacy,
                            errorMessageRes = null,
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pharmacy = null,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }

    private fun callPharmacy() {
        _state.value.pharmacy?.phoneNumber
            ?.takeIf { it.isNotBlank() }
            ?.let { sendEffect(PharmacyProfileUIEffect.DialPhone(it)) }
    }

    private fun openDirections() {
        val pharmacy = _state.value.pharmacy ?: return
        val latitude = pharmacy.latitude ?: return
        val longitude = pharmacy.longitude ?: return
        sendEffect(
            PharmacyProfileUIEffect.OpenDirections(
                latitude = latitude,
                longitude = longitude,
            )
        )
    }

    private fun sendEffect(effect: PharmacyProfileUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private companion object {
        const val DEFAULT_PHARMACY_ID = 6L
    }
}
