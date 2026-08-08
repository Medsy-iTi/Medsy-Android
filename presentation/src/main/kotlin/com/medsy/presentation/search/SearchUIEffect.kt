package com.medsy.presentation.search

import androidx.annotation.StringRes

sealed interface SearchUIEffect {
    data object NavigateBack : SearchUIEffect
    data class NavigateToProductDetails(val productId: String) : SearchUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList(),
        val isSuccess: Boolean = false,
    ) : SearchUIEffect
}
