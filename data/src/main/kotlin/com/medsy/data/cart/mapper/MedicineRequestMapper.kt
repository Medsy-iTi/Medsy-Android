package com.medsy.data.cart.mapper

import com.medsy.data.cart.remote.MedicineRequestDto
import com.medsy.data.cart.remote.MedicineRequestItemDto
import com.medsy.domain.requests.model.MedicineRequestDetails
import com.medsy.domain.requests.model.MedicineRequestItem

fun MedicineRequestDto.toDomain(): MedicineRequestDetails = MedicineRequestDetails(
    id = id,
    customerId = customerId,
    customerName = customerName,
    customerPhone = customerPhone,
    deliveryLatitude = deliveryLatitude,
    deliveryLongitude = deliveryLongitude,
    deliveryAddress = deliveryAddress,
    status = status,
    createdAt = createdAt,
    items = items.map { it.toDomain() },
    prescriptionUrl = prescriptionUrl,
    notes = notes
)

fun MedicineRequestItemDto.toDomain(): MedicineRequestItem = MedicineRequestItem(
    id = id,
    productId = productId,
    imageUrl = product?.imageUrl,
    productName = product?.productName ?: product?.name ?: "",
    strength = product?.strength,
    packSize = product?.packSize,
    form = product?.form,
    quantity = quantity,
    unitPrice = unitPrice
)
