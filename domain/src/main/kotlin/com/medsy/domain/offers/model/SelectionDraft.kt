package com.medsy.domain.offers.model

import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.orders.model.MasterOrderAllocation
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.PaymentStatus

data class SelectionDraft(
    val requestId: Long,
    val pharmacyAllocations: List<MasterOrderAllocation>,
    val deliveryFees: Double,
    val totalPrice: Double,
)

data class FulfillmentConfirmation(
    val masterOrderId: Long,
    val orderStatus: OrderStatus,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus?,
)
