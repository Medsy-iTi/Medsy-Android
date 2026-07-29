package com.medsy.presentation.orders.details

sealed interface OrderDetailsUIIntent {
    data object BackClicked : OrderDetailsUIIntent
    data object RetryClicked : OrderDetailsUIIntent
    data object PharmacyClicked : OrderDetailsUIIntent
    data object ReorderClicked : OrderDetailsUIIntent
    data object Refresh : OrderDetailsUIIntent
    data class LineItemClicked(val productId: String) : OrderDetailsUIIntent
}
