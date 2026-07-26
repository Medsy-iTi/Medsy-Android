package com.medsy.data.orders.mapper

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderItemDto
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderItemDomain
import com.medsy.domain.orders.model.OrderStatusDomain

fun OrderDetailsDto.toDomain(): OrderDetailsDomain {
    return OrderDetailsDomain(
        id = id,
        userId = userId,
        pharmacyId = pharmacyId,
        pharmacistId = pharmacistId,
        offerId = offerId,
        totalPrice = totalPrice,
        deliveryLatitude = deliveryLatitude,
        deliveryLongitude = deliveryLongitude,
        status = mapOrderStatus(status),
        date = date,
        items = items.map { it.toDomain() }
    )
}

private fun mapOrderStatus(status: String?): OrderStatusDomain {
    return when (status?.uppercase()) {
        "PENDING" -> OrderStatusDomain.Pending
        "CONFIRMED" -> OrderStatusDomain.Confirmed
        "DELIVERED" -> OrderStatusDomain.Delivered
        "CANCELLED" -> OrderStatusDomain.Cancelled
        else -> OrderStatusDomain.Pending
    }
}

