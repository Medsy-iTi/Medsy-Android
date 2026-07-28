package com.medsy.data.orders.mapper

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderItemDto
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderItemDomain
import com.medsy.domain.orders.model.OrderStatusDomain

fun OrderDetailsDto.toDomain(): OrderDetailsDomain {
    return OrderDetailsDomain(
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
        status = mapOrderStatus(status),
        date = date,
        prescriptionImage = prescriptionImage,
        customerNote = customerNote,
        pharmacyNote = pharmacyNote,
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

