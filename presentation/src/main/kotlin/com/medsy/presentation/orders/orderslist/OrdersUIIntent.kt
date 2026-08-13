package com.medsy.presentation.orders.orderslist

import com.medsy.presentation.orders.orderslist.model.OrderFilter

sealed interface OrdersUIIntent {
    data class FilterSelected(val filter: OrderFilter) : OrdersUIIntent
    data class OrderClicked(val orderId: Long) : OrdersUIIntent
    data object LoadNextPage : OrdersUIIntent
    data object Retry : OrdersUIIntent
    data object Refresh : OrdersUIIntent
    data object Resume : OrdersUIIntent
}
