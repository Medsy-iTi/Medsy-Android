package com.medsy.domain.orders.model



data class Order(
    val id: String,
    val dateLabel: String,
    val status: OrderStatus,
    val pharmacyName: String?,
    val total: Int,
    val productCount: Int,
    val productThumbnails: List<OrderProductThumbnailDomain>
)

data class OrderProductThumbnailDomain(
    val productId: String,
    val imageUrl: String?,
)
enum class OrderStatus {
    Confirmed,
    Delivered,
    Cancelled,
    Pending
}