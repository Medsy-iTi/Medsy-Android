package com.medsy.presentation.orders.details

sealed interface OrderDetailsUIEffect {
    data object NavigateBack : OrderDetailsUIEffect

    data class NavigateToPharmacyProfile(val pharmacyId: String) : OrderDetailsUIEffect

    data class ReorderRequested(val orderId: String) : OrderDetailsUIEffect
}
