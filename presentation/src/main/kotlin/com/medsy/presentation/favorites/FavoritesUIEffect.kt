package com.medsy.presentation.favorites

import androidx.annotation.StringRes

sealed interface FavoritesUIEffect {
    data object NavigateBack : FavoritesUIEffect
    data class NavigateToProductDetails(val productId: String) : FavoritesUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
        val isSuccess: Boolean = false,
    ) : FavoritesUIEffect
}
