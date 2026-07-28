package com.medsy.presentation.orders

import com.medsy.presentation.orders.model.OrderFilter
import com.medsy.presentation.orders.model.OrderSummary
import com.medsy.presentation.orders.model.matchesFilter

data class OrdersUIState(
    val isLoading: Boolean = true,
    val isLoadMore: Boolean = false,
    val selectedFilter: OrderFilter = OrderFilter.All,
    val orders: List<OrderSummary> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLastPage: Boolean = true,
    val errorMessageRes: Int? = null
) {
    val filteredOrders: List<OrderSummary>
        get() = orders.filter { it.matchesFilter(selectedFilter) }
}
