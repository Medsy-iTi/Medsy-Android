package com.medsy.domain.orders.model

data class OrderPageDomain(
    val content: List<Order>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)

data class Order(
    val id: String,
    val userId: String,
    val pharmacyId: String?,
    val pharmacistId: String?,
    val offerId: String?,
    val totalPrice: Double,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,
    val status: OrderStatus,
    val dateLabel: String,
    val items: List<OrderItemDomain>
)

data class OrderItemDomain(
    val id: String,
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
)

enum class OrderStatus {
    Confirmed,
    Delivered,
    Cancelled,
    Pending
}