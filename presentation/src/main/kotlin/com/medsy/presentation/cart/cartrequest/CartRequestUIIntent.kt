package com.medsy.presentation.cart.cartrequest

import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.PaymentOption

sealed interface CartRequestUIIntent {
    data class DeliveryMethodSelected(
        val deliveryMethod: DeliveryMethod,
    ) : CartRequestUIIntent

    data class AddressOptionSelected(
        val addressOption: CartRequestAddressOption,
    ) : CartRequestUIIntent

    data class CustomAddressChanged(val address: String) : CartRequestUIIntent
    data object LocationPickerClicked : CartRequestUIIntent
    data object LocationPickerDismissed : CartRequestUIIntent
    data class LocationSelected(
        val latitude: Double,
        val longitude: Double,
    ) : CartRequestUIIntent

    data class PaymentOptionSelected(
        val paymentOption: PaymentOption,
    ) : CartRequestUIIntent

    data object RetryCart : CartRequestUIIntent
    data object RetryProfile : CartRequestUIIntent
    data object SubmitClicked : CartRequestUIIntent
}
