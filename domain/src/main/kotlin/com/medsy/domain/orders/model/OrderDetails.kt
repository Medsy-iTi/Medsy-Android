package com.medsy.domain.orders.model


data class OrderDetailsDomain(
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
    val date: String,
    val prescriptionImage: String? = null,
    val customerNote: String? = null,
    val pharmacyNote: String? = null,
    val items: List<OrderItemDomain>
)