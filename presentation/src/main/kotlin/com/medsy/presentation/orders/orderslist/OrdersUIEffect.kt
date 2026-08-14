package com.medsy.presentation.orders.orderslist

sealed interface OrdersUIEffect {
    data class NavigateToOrderDetails(val orderId: String) : OrdersUIEffect
    data class NavigateToOrderReview(val requestId: Long, val masterOrderId: Long) : OrdersUIEffect
}
