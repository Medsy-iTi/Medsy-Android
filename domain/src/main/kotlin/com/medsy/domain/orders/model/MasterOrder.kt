package com.medsy.domain.orders.model

import com.medsy.domain.common.model.PaymentMethod

data class MasterOrderPage(
    val content: List<MasterOrder>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
)

data class MasterOrder(
    val id: Long,
    val requestId: Long,
    val pharmacyAllocations: List<MasterOrderAllocation>,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus?,
    val fulfillmentMethod: FulfillmentMethod?,
    val deliveryFee: Double,
    val totalPrice: Double,
    val orderStatus: OrderStatus,
    val paymentExpiresAt: String?,
    val paidAt: String?,
) {
    val itemSubtotal: Double
        get() = (totalPrice - deliveryFee).coerceAtLeast(0.0)

    val displayedDeliveryFee: Double
        get() = if (fulfillmentMethod == FulfillmentMethod.PICKUP) 0.0 else deliveryFee

    val displayedTotal: Double
        get() = if (fulfillmentMethod == FulfillmentMethod.PICKUP) itemSubtotal else totalPrice

    val items: List<MasterOrderItem>
        get() = pharmacyAllocations.flatMap(MasterOrderAllocation::items)
}

data class MasterOrderAllocation(
    val subOrderId: Long,
    val pharmacyId: Long,
    val pharmacyName: String,
    val latitude: Double?,
    val longitude: Double?,
    val items: List<MasterOrderItem>,
)

data class MasterOrderItem(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val product: MasterOrderProduct?,
) {
    val totalPrice: Double
        get() = quantity * unitPrice
}

data class MasterOrderProduct(
    val id: Long,
    val name: String,
    val imageUrl: String?,
)

enum class FulfillmentMethod {
    DELIVERY,
    PICKUP,
}

enum class OrderStatus {
    PENDING,
    PENDING_PAYMENT,
    PREPARING,
    READY_FOR_PICKUP,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    UNKNOWN,
}

enum class PaymentStatus {
    UNPAID,
    PENDING,
    PAID,
    FAILED,
    CANCELED,
    EXPIRED,
    UNKNOWN,
}
