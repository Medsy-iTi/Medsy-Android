package com.medsy.presentation.search

sealed interface SearchUIIntent {
    data class QueryChanged(val value: String) : SearchUIIntent
    data object ClearQueryClicked : SearchUIIntent
    data object BackClicked : SearchUIIntent
    data class FilterChipClicked(val filterId: String) : SearchUIIntent
    data class ProductClicked(val productId: String) : SearchUIIntent
    data class FavoriteClicked(val productId: String) : SearchUIIntent
    data class AddToCartClicked(val productId: String) : SearchUIIntent
}
