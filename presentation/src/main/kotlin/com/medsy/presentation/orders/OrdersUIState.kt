package com.medsy.presentation.orders

import com.medsy.presentation.orders.model.OrderFilter
import com.medsy.presentation.orders.model.OrderSummary
import com.medsy.presentation.orders.model.matchesFilter

data class OrdersUIState(
    val isLoading: Boolean = true,
    val selectedFilter: OrderFilter = OrderFilter.All,
    val orders: List<OrderSummary> = emptyList(),
) {
    val filteredOrders: List<OrderSummary>
        get() = orders.filter { it.matchesFilter(selectedFilter) }
}
