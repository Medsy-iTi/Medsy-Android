package com.medsy.presentation.productdetails

sealed interface ProductDetailsUIEffect {
    data object NavigateBack : ProductDetailsUIEffect
    data object OpenShareSheet : ProductDetailsUIEffect
    data object NavigateToPharmacistChat : ProductDetailsUIEffect
    data class ShowMessage(val messageRes: Int, val args: List<Any> = emptyList()) : ProductDetailsUIEffect
}
