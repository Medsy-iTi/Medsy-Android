package com.medsy.presentation.orders

sealed interface OrdersUIEffect {
    data class NavigateToOrderDetails(val orderId: String) : OrdersUIEffect
}
