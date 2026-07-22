package com.medsy.presentation.orders.details.model

import com.medsy.presentation.orders.model.OrderStatus

enum class FulfillmentType {
    Pickup,
    Delivery,
}

data class OrderPharmacyInfo(
    val id: Long,
    val name: String,
)

data class OrderLineItem(
    val id: Long,
    val productId: Long,
    val medicineName: String,
    val imageUrl: String?,
    val quantity: Int,
    val unitPrice: Int,
    val alternativeToMedicineName: String? = null,
) {
    val lineTotal: Int get() = quantity * unitPrice
}

data class OrderDetails(
    val id: Long,
    val status: OrderStatus,
    val dateLabel: String,
    val fulfillmentType: FulfillmentType,
    val pharmacy: OrderPharmacyInfo?,
    val lineItems: List<OrderLineItem>,
    val itemsSubtotal: Int,
    val deliveryFee: Int?,
    val finalTotal: Int,
)