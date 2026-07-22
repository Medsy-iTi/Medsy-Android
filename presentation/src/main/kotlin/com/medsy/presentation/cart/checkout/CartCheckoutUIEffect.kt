package com.medsy.presentation.cart.checkout

import androidx.annotation.StringRes

sealed interface CartCheckoutUIEffect {
    data object NavigateHome : CartCheckoutUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
    ) : CartCheckoutUIEffect
}
