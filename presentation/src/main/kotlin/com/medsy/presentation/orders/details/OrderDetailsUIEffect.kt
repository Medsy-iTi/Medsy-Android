package com.medsy.presentation.orders.details

sealed interface OrderDetailsUIEffect {
    data object NavigateBack : OrderDetailsUIEffect

    data class NavigateToPharmacyProfile(val pharmacyId: Long) : OrderDetailsUIEffect

    data object ReorderRequested : OrderDetailsUIEffect

    data class NavigateToProductDetails(val productId: String) : OrderDetailsUIEffect

    data class ShowErrorSnackbar(val messageRes: Int) : OrderDetailsUIEffect
}
