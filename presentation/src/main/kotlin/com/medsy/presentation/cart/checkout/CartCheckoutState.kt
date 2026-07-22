package com.medsy.presentation.cart.checkout

import androidx.annotation.StringRes
import com.medsy.domain.cart.model.CartDraft
import com.medsy.domain.cart.model.CartItem
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.PaymentOption

private const val CairoLatitude = 30.0444
private const val CairoLongitude = 31.2357

enum class CheckoutAddressOption {
    DEFAULT,
    CUSTOM,
}

data class CartCheckoutState(
    val isCartLoading: Boolean = true,
    val items: List<CartItem> = emptyList(),
    val totalPriceEgp: Double = 0.0,
    val draft: CartDraft = CartDraft(),
    @StringRes val cartErrorMessageRes: Int? = null,
    val isProfileLoading: Boolean = true,
    @StringRes val profileErrorMessageRes: Int? = null,
    val defaultAddress: String? = null,
    val defaultLatitude: Double = CairoLatitude,
    val defaultLongitude: Double = CairoLongitude,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.DELIVERY,
    val addressOption: CheckoutAddressOption = CheckoutAddressOption.DEFAULT,
    val customAddress: String = "",
    val customLatitude: Double = CairoLatitude,
    val customLongitude: Double = CairoLongitude,
    val hasConfirmedCustomLocation: Boolean = false,
    val isMapPickerVisible: Boolean = false,
    val paymentOption: PaymentOption = PaymentOption.CASH,
    val isSubmitting: Boolean = false,
) {
    val hasRequestContent: Boolean
        get() = items.isNotEmpty() || draft.prescriptionImage != null

    val hasDefaultAddress: Boolean
        get() = !defaultAddress.isNullOrBlank()

    val isDeliveryAddressValid: Boolean
        get() = when {
            deliveryMethod == DeliveryMethod.PICKUP -> true
            addressOption == CheckoutAddressOption.DEFAULT -> hasDefaultAddress
            else -> customAddress.isNotBlank() && hasConfirmedCustomLocation
        }

    val isSubmitEnabled: Boolean
        get() = !isCartLoading && cartErrorMessageRes == null && hasRequestContent &&
            isDeliveryAddressValid && !isSubmitting &&
            (deliveryMethod == DeliveryMethod.PICKUP || !isProfileLoading)
}
