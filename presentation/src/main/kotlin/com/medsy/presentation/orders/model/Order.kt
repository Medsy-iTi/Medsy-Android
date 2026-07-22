package com.medsy.presentation.orders.model

enum class OrderStatus {
    Confirmed,
    Delivered,
    Cancelled,
}

enum class OrderFilter {
    All,
    Active,
    Finished,
    Cancelled,
}

data class OrderProductThumbnail(
    val productId: Long,
    val imageUrl: String?,
)

data class OrderSummary(
    val id: String,
    // TODO: replace with a real formatted date once the backend provides
    // order timestamps — kept as a plain label for now (e.g. "Today").
    val dateLabel: String,
    val status: OrderStatus,
    val pharmacyName: String?,
    val total: Int,
    val productCount: Int,
    val productThumbnails: List<OrderProductThumbnail>,
)

private fun OrderStatus.toFilter(): OrderFilter = when (this) {
    OrderStatus.Confirmed -> OrderFilter.Active
    OrderStatus.Delivered -> OrderFilter.Finished
    OrderStatus.Cancelled -> OrderFilter.Cancelled
}

fun OrderSummary.matchesFilter(filter: OrderFilter): Boolean =
    filter == OrderFilter.All || status.toFilter() == filter
