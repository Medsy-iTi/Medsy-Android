package com.medsy.presentation.cart

import androidx.annotation.StringRes

sealed interface CartUIEffect {
    data object OpenPrescription : CartUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
    ) : CartUIEffect
}
