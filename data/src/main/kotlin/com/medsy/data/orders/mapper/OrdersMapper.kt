package com.medsy.data.orders.mapper

import com.medsy.data.remote.model.OrderDto
import com.medsy.data.remote.model.OrderItemDto
import com.medsy.data.remote.model.OrderPageDataDto
import com.medsy.domain.orders.model.Order
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.model.OrderItemDomain


fun OrderPageDataDto.toDomain(): OrderPageDomain {
    return OrderPageDomain(
        content = content.map { it.toDomain() },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        last = last
    )
}

fun OrderDto.toDomain(): Order {
    return Order(
        id = id.toString(),
        userId = userId.toString(),
        pharmacyId = pharmacyId?.toString(),
        pharmacistId = pharmacistId?.toString(),
        offerId = offerId?.toString(),
        totalPrice = totalPrice,
        deliveryLatitude = deliveryLatitude,
        deliveryLongitude = deliveryLongitude,
        status = status.toOrderStatus(),
        dateLabel = date,
        items = items.map { it.toDomain() }
    )
}

fun OrderItemDto.toDomain(): OrderItemDomain {
    return OrderItemDomain(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice
    )
}

private fun String.toOrderStatus(): OrderStatus {
    return when (this.uppercase()) {
        "CONFIRMED" -> OrderStatus.Confirmed
        "DELIVERED" -> OrderStatus.Delivered
        "CANCELLED", "CANCELED" -> OrderStatus.Cancelled
        "PENDING" -> OrderStatus.Pending
        else -> OrderStatus.Pending
    }
}