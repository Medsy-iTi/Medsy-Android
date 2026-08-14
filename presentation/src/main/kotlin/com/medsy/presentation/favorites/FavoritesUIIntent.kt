package com.medsy.presentation.favorites

sealed interface FavoritesUIIntent {
    data class ProductClicked(val productId: String) : FavoritesUIIntent
    data class FavoriteClicked(val productId: String) : FavoritesUIIntent
    data class AddToCartClicked(val productId: String) : FavoritesUIIntent
    data object BackClicked : FavoritesUIIntent
}
