package com.medsy.presentation.products

sealed interface ProductsUIEffect {
    object NavigateBack : ProductsUIEffect
    data class NavigateToProductDetails(val productId: Int) : ProductsUIEffect
}
