package com.medsy.presentation.favorites

sealed interface FavoritesUIEffect {
    data object NavigateBack : FavoritesUIEffect
    data class NavigateToProductDetails(val productId: String) : FavoritesUIEffect
    data class ShowMessage(val messageRes: Int, val args: List<Any> = emptyList()) : FavoritesUIEffect
}
