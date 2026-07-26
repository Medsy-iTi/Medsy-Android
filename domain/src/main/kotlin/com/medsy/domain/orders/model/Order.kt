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
    val id: Long,
    val userId: Long,
    val pharmacyId: Long?,
    val pharmacistId: Long?,
    val offerId: Long?,
    val totalPrice: Double,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val status: OrderStatusDomain,
    val dateLabel: String,
    val items: List<OrderItemDomain>
)

data class OrderItemDomain(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val productName: String? = null,
    val imageUrl: String? = null
)

enum class OrderStatusDomain {
    Confirmed,
    Delivered,
    Cancelled,
    Pending
}