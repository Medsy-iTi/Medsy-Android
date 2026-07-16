package com.medsy.presentation.search

sealed interface SearchUIEffect {
    data object NavigateBack : SearchUIEffect
    data class NavigateToProductDetails(val productId: String) : SearchUIEffect
    data class ShowMessage(val messageRes: Int) : SearchUIEffect
}
