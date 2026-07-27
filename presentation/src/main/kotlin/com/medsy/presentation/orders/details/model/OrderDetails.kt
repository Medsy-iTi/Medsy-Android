package com.medsy.presentation.orders.details.model

import com.medsy.presentation.orders.model.OrderStatus

enum class FulfillmentType {
    Pickup,
    Delivery,
}

data class OrderPharmacyInfo(
    val id: Long,
    val name: String,
    val address: String? = null,
    val phone: String? = null,
)

data class OrderLineItem(
    val id: Long,
    val productId: Long,
    val medicineName: String,
    val imageUrl: String?,
    val quantity: Int,
    val unitPrice: Double,
    val alternativeToMedicineName: String? = null,
) {
    val lineTotal: Double get() = quantity * unitPrice
}

data class OrderDetails(
    val id: Long,
    val status: OrderStatus,
    val dateLabel: String,
    val fulfillmentType: FulfillmentType,
    val pharmacy: OrderPharmacyInfo?,
    val lineItems: List<OrderLineItem>,
    val itemsSubtotal: Double,
    val deliveryFee: Double?,
    val finalTotal: Double,
)