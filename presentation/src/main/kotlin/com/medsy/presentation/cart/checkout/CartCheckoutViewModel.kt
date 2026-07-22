package com.medsy.presentation.cart.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.cart.usecase.GetCartUseCase
import com.medsy.domain.cart.usecase.ObserveCartDraftUseCase
import com.medsy.domain.cart.usecase.SubmitProductsRequestUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CartCheckoutViewModel @Inject constructor(
    private val getCart: GetCartUseCase,
    private val observeCartDraft: ObserveCartDraftUseCase,
    private val getProfile: GetProfileUseCase,
    private val submitProductsRequest: SubmitProductsRequestUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CartCheckoutState())
    val state = _state
        .onStart { loadCheckout() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CartCheckoutState(),
        )

    private val _effect = Channel<CartCheckoutUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeCartDraft().collect { draft ->
                _state.update { it.copy(draft = draft) }
            }
        }
    }

    fun onIntent(intent: CartCheckoutUIIntent) {
        if (_state.value.isSubmitting) return
        when (intent) {
            is CartCheckoutUIIntent.DeliveryMethodSelected -> _state.update {
                it.copy(deliveryMethod = intent.deliveryMethod)
            }

            is CartCheckoutUIIntent.AddressOptionSelected -> {
                if (intent.addressOption == CheckoutAddressOption.DEFAULT &&
                    !_state.value.hasDefaultAddress
                ) return
                _state.update { it.copy(addressOption = intent.addressOption) }
            }

            is CartCheckoutUIIntent.CustomAddressChanged -> _state.update {
                it.copy(customAddress = intent.address)
            }

            CartCheckoutUIIntent.LocationPickerClicked -> _state.update {
                it.copy(isMapPickerVisible = true)
            }

            CartCheckoutUIIntent.LocationPickerDismissed -> _state.update {
                it.copy(isMapPickerVisible = false)
            }

            is CartCheckoutUIIntent.LocationSelected -> _state.update {
                it.copy(
                    isMapPickerVisible = false,
                    customLatitude = intent.latitude,
                    customLongitude = intent.longitude,
                    hasConfirmedCustomLocation = true,
                )
            }

            is CartCheckoutUIIntent.PaymentOptionSelected -> _state.update {
                it.copy(paymentOption = intent.paymentOption)
            }

            CartCheckoutUIIntent.RetryCart -> viewModelScope.launch { loadCart() }
            CartCheckoutUIIntent.RetryProfile -> viewModelScope.launch { loadProfile() }
            CartCheckoutUIIntent.SubmitClicked -> submit()
        }
    }

    private suspend fun loadCheckout() = coroutineScope {
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
                            CheckoutAddressOption.CUSTOM
                        } else if (
                            state.customAddress.isBlank() &&
                            !state.hasConfirmedCustomLocation
                        ) {
                            CheckoutAddressOption.DEFAULT
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
                        addressOption = CheckoutAddressOption.CUSTOM,
                    )
                }
            }
    }

    private fun submit() {
        val currentState = _state.value
        if (!currentState.isSubmitEnabled) return

        val isDelivery = currentState.deliveryMethod == DeliveryMethod.DELIVERY
        val usesDefaultAddress = currentState.addressOption == CheckoutAddressOption.DEFAULT
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
                    note = currentState.draft.pharmacistNote,
                    prescriptionImage = currentState.draft.prescriptionImage,
                    deliveryMethod = currentState.deliveryMethod,
                    deliveryAddress = address,
                    latitude = latitude,
                    longitude = longitude,
                    paymentOption = currentState.paymentOption,
                )
            ).onSuccess {
                _state.update { it.copy(isSubmitting = false) }
                _effect.send(CartCheckoutUIEffect.NavigateHome)
            }.onError { error ->
                _state.update { it.copy(isSubmitting = false) }
                _effect.send(CartCheckoutUIEffect.ShowMessage(error.toMessageRes()))
            }
        }
    }
}
