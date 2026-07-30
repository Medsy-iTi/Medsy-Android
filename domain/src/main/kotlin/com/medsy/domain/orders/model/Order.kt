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
    val customerId: Long,
    val customerName: String?,
    val pharmacyId: Long?,
    val pharmacyName: String?,
    val pharmacyAddress: String?,
    val pharmacyPhone: String?,
    val pharmacistId: Long?,
    val pharmacistName: String?,
    val offerId: Long?,
    val subTotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val status: OrderStatusDomain,
    val dateLabel: String,
    val prescriptionImage: String? = null,
    val customerNote: String? = null,
    val pharmacyNote: String? = null,
    val items: List<OrderItemDomain>
)

data class OrderItemDomain(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double? = null,
    val productName: String? = null,
    val imageUrl: String? = null
)

enum class OrderStatusDomain {
    Confirmed,
    Delivered,
    Cancelled,
    Pending
}