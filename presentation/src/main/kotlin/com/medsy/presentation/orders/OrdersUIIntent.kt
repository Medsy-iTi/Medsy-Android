package com.medsy.presentation.orders

import com.medsy.presentation.orders.model.OrderFilter

sealed interface OrdersUIIntent {
    data class FilterSelected(val filter: OrderFilter) : OrdersUIIntent
    data class OrderClicked(val orderId: String) : OrdersUIIntent
    data object LoadNextPage : OrdersUIIntent
    data object Retry : OrdersUIIntent
}
