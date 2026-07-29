package com.medsy.presentation.orders.orderslist

import com.medsy.presentation.orders.orderslist.model.OrderFilter
import com.medsy.presentation.orders.orderslist.model.OrderSummary
import com.medsy.presentation.orders.orderslist.model.matchesFilter

data class OrdersUIState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
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
