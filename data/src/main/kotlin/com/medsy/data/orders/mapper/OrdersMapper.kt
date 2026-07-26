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
        id = id,
        userId = userId,
        pharmacyId = pharmacyId,
        pharmacistId = pharmacistId,
        offerId = offerId,
        totalPrice = totalPrice,
        deliveryLatitude = deliveryLatitude,
        deliveryLongitude = deliveryLongitude,
        status = status.toOrderStatusDomain(),
        dateLabel = date ?: "",
        items = items?.map { it.toDomain() } ?: emptyList()
    )
}

fun OrderItemDto.toDomain(): OrderItemDomain {
    return OrderItemDomain(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        productName = productName,
        imageUrl = imageUrl
    )
}

private fun String?.toOrderStatusDomain(): OrderStatusDomain {
    return when (this?.uppercase()) {
        "CONFIRMED" -> OrderStatusDomain.Confirmed
        "DELIVERED" -> OrderStatusDomain.Delivered
        "CANCELLED", "CANCELED" -> OrderStatusDomain.Cancelled
        "PENDING" -> OrderStatusDomain.Pending
        else -> OrderStatusDomain.Pending
    }
}