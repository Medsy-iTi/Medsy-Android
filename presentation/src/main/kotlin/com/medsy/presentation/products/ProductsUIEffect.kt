package com.medsy.presentation.products

import androidx.annotation.StringRes

sealed interface ProductsUIEffect {
    object NavigateBack : ProductsUIEffect
    data class NavigateToProductDetails(val productId: Int) : ProductsUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val isSuccess: Boolean = false
    ) : ProductsUIEffect
}
