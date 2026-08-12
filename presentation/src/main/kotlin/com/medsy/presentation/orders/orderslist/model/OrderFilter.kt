package com.medsy.presentation.orders.orderslist.model

import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.OrderStatus

enum class OrderFilter {
    All,
    Active,
    Finished,
    Cancelled,
}

fun MasterOrder.matchesFilter(filter: OrderFilter): Boolean = when (filter) {
    OrderFilter.All -> true
    OrderFilter.Active -> orderStatus in setOf(
        OrderStatus.PENDING,
        OrderStatus.PENDING_PAYMENT,
        OrderStatus.PREPARING,
        OrderStatus.READY_FOR_PICKUP,
        OrderStatus.READY_FOR_DELIVERY,
        OrderStatus.OUT_FOR_DELIVERY,
    )
    OrderFilter.Finished -> orderStatus == OrderStatus.DELIVERED
    OrderFilter.Cancelled -> orderStatus == OrderStatus.CANCELLED
}
