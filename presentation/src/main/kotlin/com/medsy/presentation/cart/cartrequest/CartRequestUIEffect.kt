package com.medsy.presentation.cart.cartrequest

import androidx.annotation.StringRes

sealed interface CartRequestUIEffect {
    data object NavigateHome : CartRequestUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
    ) : CartRequestUIEffect
}
