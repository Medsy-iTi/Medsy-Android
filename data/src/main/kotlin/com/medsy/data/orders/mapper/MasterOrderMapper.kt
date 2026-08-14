package com.medsy.data.orders.mapper

import com.medsy.data.offers.remote.AllocatedOrderItemDto
import com.medsy.data.offers.remote.PharmacyAllocationDto
import com.medsy.data.orders.model.MasterOrderDto
import com.medsy.data.orders.model.MasterOrderPageDto
import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.orders.model.FulfillmentMethod
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.MasterOrderAllocation
import com.medsy.domain.orders.model.MasterOrderItem
import com.medsy.domain.orders.model.MasterOrderPage
import com.medsy.domain.orders.model.MasterOrderProduct
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.PaymentStatus

fun MasterOrderPageDto.toDomain(): MasterOrderPage = MasterOrderPage(
    content = content.map(MasterOrderDto::toDomain),
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages,
    last = last,
)

fun MasterOrderDto.toDomain(): MasterOrder = MasterOrder(
    id = id,
    requestId = requestId,
    pharmacyAllocations = orderResponses.map(PharmacyAllocationDto::toDomain),
    paymentMethod = paymentMethod.toPaymentMethod(),
    paymentStatus = paymentStatus?.toPaymentStatus(),
    fulfillmentMethod = fulfillmentMethod?.toFulfillmentMethod(),
    deliveryFee = deliveryFee ?: 0.0,
    totalPrice = totalPrice,
    orderStatus = orderStatus.toOrderStatus(),
    paymentExpiresAt = paymentExpiresAt,
    paidAt = paidAt,
)

fun PharmacyAllocationDto.toDomain(): MasterOrderAllocation = MasterOrderAllocation(
    subOrderId = offerId,
    pharmacyId = pharmacyId,
    pharmacyName = pharmacyName.orEmpty(),
    latitude = latitude,
    longitude = longitude,
    items = items.map(AllocatedOrderItemDto::toDomain),
)

fun AllocatedOrderItemDto.toDomain(): MasterOrderItem = MasterOrderItem(
    id = id,
    productId = productId,
    quantity = quantity,
    unitPrice = unitPrice,
    product = product?.let {
        MasterOrderProduct(
            id = it.id,
            name = it.name.ifBlank { it.productName.orEmpty() },
            imageUrl = it.imageUrl,
        )
    },
)

fun String.toPaymentMethod(): PaymentMethod = when (uppercase()) {
    "CASH" -> PaymentMethod.CASH
    "CARD", "VISA" -> PaymentMethod.CARD
    else -> PaymentMethod.UNKNOWN
}

fun String.toOrderStatus(): OrderStatus =
    runCatching { OrderStatus.valueOf(uppercase()) }.getOrDefault(OrderStatus.UNKNOWN)

fun String.toPaymentStatus(): PaymentStatus =
    runCatching { PaymentStatus.valueOf(uppercase()) }.getOrDefault(PaymentStatus.UNKNOWN)

fun String.toFulfillmentMethod(): FulfillmentMethod? =
    runCatching { FulfillmentMethod.valueOf(uppercase()) }.getOrNull()
