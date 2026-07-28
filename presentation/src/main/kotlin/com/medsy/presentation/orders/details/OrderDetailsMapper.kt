package com.medsy.presentation.orders.details

import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderStatusDomain
import com.medsy.presentation.common.util.formatOrderDate
import com.medsy.presentation.orders.details.model.FulfillmentType
import com.medsy.presentation.orders.details.model.OrderDetails
import com.medsy.presentation.orders.details.model.OrderLineItem
import com.medsy.presentation.orders.details.model.OrderPharmacyInfo
import com.medsy.presentation.orders.model.OrderStatus

fun OrderDetailsDomain.toPresentation(): OrderDetails {
    val presentationStatus = when (status) {
        OrderStatusDomain.Confirmed -> OrderStatus.Confirmed
        OrderStatusDomain.Delivered -> OrderStatus.Delivered
        OrderStatusDomain.Cancelled -> OrderStatus.Cancelled
        OrderStatusDomain.Pending -> OrderStatus.Confirmed
    }

    return OrderDetails(
        id = id,
        status = presentationStatus,
        dateLabel = formatOrderDate(date),
        fulfillmentType = FulfillmentType.Delivery,
        pharmacy = pharmacyId?.let {
            OrderPharmacyInfo(
                id = it,
                name = pharmacyName ?: "",
                address = pharmacyAddress,
                phone = pharmacyPhone
            )
        },
        lineItems = items.map { item ->
            OrderLineItem(
                id = item.id,
                medicineName = item.productName ?: "",
                imageUrl = item.imageUrl,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                alternativeToMedicineName = null,
                productId = item.productId
            )
        },
        itemsSubtotal = subTotal,
        deliveryFee = deliveryFee,
        finalTotal = total,
        prescriptionImage = prescriptionImage,
        customerNote = customerNote,
        pharmacyNote = pharmacyNote
    )
}
