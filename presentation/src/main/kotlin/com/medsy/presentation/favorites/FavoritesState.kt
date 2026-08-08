package com.medsy.presentation.favorites

import com.medsy.presentation.search.SearchProductUi

data class FavoritesState(
    val isLoading: Boolean = false,
    val products: List<SearchProductUi> = emptyList(),
    val errorMessage: Int? = null,
) {
    val isEmpty: Boolean get() = !isLoading && products.isEmpty()
}
