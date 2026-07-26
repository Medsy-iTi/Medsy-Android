package com.medsy.presentation.cart.cartrequest

import androidx.annotation.StringRes
import com.medsy.domain.cart.model.CartDraft
import com.medsy.domain.cart.model.CartItem
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.PaymentMethod

private const val CairoLatitude = 30.0444
private const val CairoLongitude = 31.2357

enum class CartRequestAddressOption {
    DEFAULT,
    CUSTOM,
}

data class CartRequestState(
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
    val addressOption: CartRequestAddressOption = CartRequestAddressOption.DEFAULT,
    val customAddress: String = "",
    val customLatitude: Double = CairoLatitude,
    val customLongitude: Double = CairoLongitude,
    val hasConfirmedCustomLocation: Boolean = false,
    val isMapPickerVisible: Boolean = false,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val isSubmitting: Boolean = false,
) {
    val hasProducts: Boolean
        get() = items.isNotEmpty()

    val hasDefaultAddress: Boolean
        get() = !defaultAddress.isNullOrBlank()

    val isDeliveryAddressValid: Boolean
        get() = when {
            deliveryMethod == DeliveryMethod.PICKUP -> true
            addressOption == CartRequestAddressOption.DEFAULT -> hasDefaultAddress
            else -> customAddress.isNotBlank() && hasConfirmedCustomLocation
        }

    val isSubmitEnabled: Boolean
        get() = !isCartLoading && cartErrorMessageRes == null && hasProducts &&
                isDeliveryAddressValid && !isSubmitting &&
                (deliveryMethod == DeliveryMethod.PICKUP || !isProfileLoading)
}
