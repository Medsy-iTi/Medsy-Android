package com.medsy.presentation.cart.cartrequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.cart.usecase.ClearCartDraftUseCase
import com.medsy.domain.cart.usecase.GetCartUseCase
import com.medsy.domain.cart.usecase.ObserveCartDraftUseCase
import com.medsy.domain.cart.usecase.SubmitProductsRequestUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartRequestViewModel @Inject constructor(
    private val getCart: GetCartUseCase,
    private val observeCartDraft: ObserveCartDraftUseCase,
    private val clearCartDraft: ClearCartDraftUseCase,
    private val getProfile: GetProfileUseCase,
    private val submitProductsRequest: SubmitProductsRequestUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CartRequestState())
    val state = _state
        .onStart { loadRequest() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CartRequestState(),
        )

    private val _effect = Channel<CartRequestUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeCartDraft().collect { draft ->
                _state.update { it.copy(draft = draft) }
            }
        }
    }

    fun onIntent(intent: CartRequestUIIntent) {
        if (_state.value.isSubmitting) return
        when (intent) {
            is CartRequestUIIntent.DeliveryMethodSelected -> _state.update {
                it.copy(deliveryMethod = intent.deliveryMethod)
            }

            is CartRequestUIIntent.AddressOptionSelected -> {
                if (intent.addressOption == CartRequestAddressOption.DEFAULT &&
                    !_state.value.hasDefaultAddress
                ) return
                _state.update { it.copy(addressOption = intent.addressOption) }
            }

            is CartRequestUIIntent.CustomAddressChanged -> _state.update {
                it.copy(customAddress = intent.address)
            }

            CartRequestUIIntent.LocationPickerClicked -> _state.update {
                it.copy(isMapPickerVisible = true)
            }

            CartRequestUIIntent.LocationPickerDismissed -> _state.update {
                it.copy(isMapPickerVisible = false)
            }

            is CartRequestUIIntent.LocationSelected -> _state.update {
                it.copy(
                    isMapPickerVisible = false,
                    customLatitude = intent.latitude,
                    customLongitude = intent.longitude,
                    hasConfirmedCustomLocation = true,
                )
            }

            is CartRequestUIIntent.PaymentOptionSelected -> _state.update {
                it.copy(paymentOption = intent.paymentOption)
            }

            CartRequestUIIntent.RetryCart -> viewModelScope.launch { loadCart() }
            CartRequestUIIntent.RetryProfile -> viewModelScope.launch { loadProfile() }
            CartRequestUIIntent.SubmitClicked -> submit()
        }
    }

    private suspend fun loadRequest() = coroutineScope {
        launch { loadCart() }
        launch { loadProfile() }
    }

    private suspend fun loadCart() {
        _state.update {
            it.copy(
                isCartLoading = true,
                cartErrorMessageRes = null,
            )
        }
        getCart()
            .onSuccess { cart ->
                _state.update {
                    it.copy(
                        isCartLoading = false,
                        items = cart.items,
                        totalPriceEgp = cart.totalPriceEgp,
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        isCartLoading = false,
                        cartErrorMessageRes = error.toMessageRes(),
                    )
                }
            }
    }

    private suspend fun loadProfile() {
        _state.update {
            it.copy(
                isProfileLoading = true,
                profileErrorMessageRes = null,
            )
        }
        getProfile()
            .onSuccess { profile ->
                val address = profile.homeAddress?.trim()?.takeIf(String::isNotBlank)
                val latitude = profile.latitude?.takeIf { it in -90.0..90.0 }
                val longitude = profile.longitude?.takeIf { it in -180.0..180.0 }
                _state.update { state ->
                    state.copy(
                        isProfileLoading = false,
                        defaultAddress = address,
                        defaultLatitude = if (latitude != null && longitude != null) {
                            latitude
                        } else {
                            state.defaultLatitude
                        },
                        defaultLongitude = if (latitude != null && longitude != null) {
                            longitude
                        } else {
                            state.defaultLongitude
                        },
                        addressOption = if (address == null) {
                            CartRequestAddressOption.CUSTOM
                        } else if (
                            state.customAddress.isBlank() &&
                            !state.hasConfirmedCustomLocation
                        ) {
                            CartRequestAddressOption.DEFAULT
                        } else {
                            state.addressOption
                        },
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        isProfileLoading = false,
                        profileErrorMessageRes = error.toMessageRes(),
                        addressOption = CartRequestAddressOption.CUSTOM,
                    )
                }
            }
    }

    private fun submit() {
        val currentState = _state.value
        if (!currentState.isSubmitEnabled) return

        val isDelivery = currentState.deliveryMethod == DeliveryMethod.DELIVERY
        val usesDefaultAddress = currentState.addressOption == CartRequestAddressOption.DEFAULT
        val address = when {
            !isDelivery -> null
            usesDefaultAddress -> currentState.defaultAddress
            else -> currentState.customAddress
        }
        val latitude = when {
            !isDelivery -> null
            usesDefaultAddress -> currentState.defaultLatitude
            else -> currentState.customLatitude
        }
        val longitude = when {
            !isDelivery -> null
            usesDefaultAddress -> currentState.defaultLongitude
            else -> currentState.customLongitude
        }

        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            submitProductsRequest(
                ProductsRequest(
                    items = currentState.items.map { item ->
                        CartItemInput(
                            productId = item.productId,
                            quantity = item.quantity,
                        )
                    },
                    notes = currentState.draft.pharmacistNote,
                    prescriptionImage = currentState.draft.prescriptionImage,
                    deliveryMethod = currentState.deliveryMethod,
                    deliveryAddress = address,
                    deliveryLatitude = latitude,
                    deliveryLongitude = longitude,
                    paymentMethod = currentState.paymentOption,
                )
            ).onSuccess {
                clearCartDraft()
                _state.update { it.copy(isSubmitting = false) }
                _effect.send(CartRequestUIEffect.NavigateHome)
            }.onError { error ->
                _state.update { it.copy(isSubmitting = false) }
                _effect.send(CartRequestUIEffect.ShowMessage(error.toMessageRes()))
            }
        }
    }
}