package com.medsy.domain.orders.model


data class OrderDetailsDomain(
    val id: Long,
    val userId: Long,
    val pharmacyId: Long?,
    val pharmacistId: Long?,
    val offerId: Long?,
    val totalPrice: Double,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val status: OrderStatusDomain,
    val date: String,
    val items: List<OrderItemDomain>
)