package com.medsy.presentation.products

sealed interface ProductsUIIntent {
    data class LoadProducts(val categoryId: Int, val categoryName: String) : ProductsUIIntent
    data class OnSearchQueryChange(val query: String) : ProductsUIIntent
    object OnBackClick : ProductsUIIntent
    data class OnProductClick(val productId: Int) : ProductsUIIntent
}
