package com.medsy.data.orders.mapper

import com.medsy.data.remote.model.OrderDto
import com.medsy.domain.orders.model.Order
import com.medsy.domain.orders.model.OrderProductThumbnailDomain
import com.medsy.domain.orders.model.OrderStatus


fun OrderDto.toDomain(): Order {
    val mappedStatus = when (status.uppercase()) {
        "PENDING" -> OrderStatus.Pending
        "CONFIRMED" -> OrderStatus.Confirmed
        "DELIVERED", "FINISHED" -> OrderStatus.Delivered
        "CANCELLED" -> OrderStatus.Cancelled
        else -> OrderStatus.Pending
    }

    val thumbnails = items.map { item ->
        OrderProductThumbnailDomain(
            productId = item.productId.toString(),
            imageUrl = null
        )
    }

    return Order(
        id = id.toString(),
        dateLabel = date,
        status = mappedStatus,
        pharmacyName = pharmacyId?.let { "Pharmacy #$it" },
        total = totalPrice.toInt(),
        productCount = items.sumOf { it.quantity },
        productThumbnails = thumbnails
    )
}