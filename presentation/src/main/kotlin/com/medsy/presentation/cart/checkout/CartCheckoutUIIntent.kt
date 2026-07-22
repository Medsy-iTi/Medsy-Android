package com.medsy.presentation.cart.checkout

import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.PaymentOption

sealed interface CartCheckoutUIIntent {
    data class DeliveryMethodSelected(
        val deliveryMethod: DeliveryMethod,
    ) : CartCheckoutUIIntent

    data class AddressOptionSelected(
        val addressOption: CheckoutAddressOption,
    ) : CartCheckoutUIIntent

    data class CustomAddressChanged(val address: String) : CartCheckoutUIIntent
    data object LocationPickerClicked : CartCheckoutUIIntent
    data object LocationPickerDismissed : CartCheckoutUIIntent
    data class LocationSelected(
        val latitude: Double,
        val longitude: Double,
    ) : CartCheckoutUIIntent

    data class PaymentOptionSelected(
        val paymentOption: PaymentOption,
    ) : CartCheckoutUIIntent

    data object RetryCart : CartCheckoutUIIntent
    data object RetryProfile : CartCheckoutUIIntent
    data object SubmitClicked : CartCheckoutUIIntent
}
