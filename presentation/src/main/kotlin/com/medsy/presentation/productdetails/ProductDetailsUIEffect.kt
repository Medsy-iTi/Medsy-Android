package com.medsy.presentation.productdetails

import androidx.annotation.StringRes

sealed interface ProductDetailsUIEffect {
    data object NavigateBack : ProductDetailsUIEffect
    data object OpenShareSheet : ProductDetailsUIEffect
    data object NavigateToPharmacistChat : ProductDetailsUIEffect
    data object NavigateToCart : ProductDetailsUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
        val isSuccess: Boolean = false,
    ) : ProductDetailsUIEffect
}
