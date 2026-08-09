package com.medsy.data.orders.mapper

import com.medsy.data.orders.model.OrderDto
import com.medsy.data.orders.model.OrderItemDto
import com.medsy.data.orders.model.OrderPageDataDto
import com.medsy.domain.orders.model.Order
import com.medsy.domain.orders.model.OrderStatusDomain
import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.model.OrderItemDomain

fun OrderPageDataDto.toDomain(): OrderPageDomain {
    return OrderPageDomain(
        content = content.flatMap { group -> group.orders.map(OrderDto::toDomain) },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        last = last
    )
}

fun OrderDto.toDomain(): Order {
    return Order(
        id = id,
        customerId = customerId,
        customerName = customerName,
        pharmacyId = pharmacyId,
        pharmacyName = pharmacyName,
        pharmacyAddress = pharmacyAddress,
        pharmacyPhone = pharmacyPhone,
        pharmacistId = pharmacistId,
        pharmacistName = pharmacistName,
        offerId = offerId,
        subTotal = subTotal,
        deliveryFee = deliveryFee,
        total = total,
        deliveryLatitude = deliveryLatitude,
        deliveryLongitude = deliveryLongitude,
        status = status.toOrderStatusDomain(),
        dateLabel = createdAt.orEmpty(),
        prescriptionImage = prescriptionUrl,
        customerNote = customerNotes,
        pharmacyNote = null,
        items = items.map { it.toDomain() },
    )
}

fun OrderItemDto.toDomain(): OrderItemDomain {
    return OrderItemDomain(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        productName = product.name.ifBlank { product.productName.orEmpty() },
        imageUrl = product.imageUrl?.takeIf(String::isNotBlank),
    )
}

private fun String?.toOrderStatusDomain(): OrderStatusDomain {
    return when (this?.uppercase()) {
        "CONFIRMED", "PREPARING", "READY_FOR_PICKUP", "OUT_FOR_DELIVERY" ->
            OrderStatusDomain.Confirmed
        "DELIVERED" -> OrderStatusDomain.Delivered
        "CANCELLED", "CANCELED" -> OrderStatusDomain.Cancelled
        "PENDING" -> OrderStatusDomain.Pending
        else -> OrderStatusDomain.Pending
    }
}
