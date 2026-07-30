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
        dateLabel = date ?: "",
        prescriptionImage = prescriptionImage,
        customerNote = customerNote,
        pharmacyNote = pharmacyNote,
        items = items?.map { it.toDomain() } ?: emptyList()
    )
}

fun OrderItemDto.toDomain(): OrderItemDomain {
    return OrderItemDomain(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
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