package com.medsy.presentation.products

import androidx.annotation.StringRes

sealed interface ProductsUIEffect {
    data object NavigateBack : ProductsUIEffect
    data class NavigateToProductDetails(val productId: Int) : ProductsUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
        val isSuccess: Boolean = false,
    ) : ProductsUIEffect
}
