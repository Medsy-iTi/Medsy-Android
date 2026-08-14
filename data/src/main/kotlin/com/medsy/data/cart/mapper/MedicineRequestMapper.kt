package com.medsy.data.cart.mapper

import com.medsy.data.cart.remote.MedicineRequestDto
import com.medsy.data.cart.remote.MedicineRequestItemDto
import com.medsy.data.cart.remote.MedicineRequestPageDto
import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.model.MedicineRequestItem
import com.medsy.domain.requests.model.MedicineRequestStatus
import com.medsy.domain.requests.model.MedicineRequestPage
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun MedicineRequestDto.toDomain(): MedicineRequest = MedicineRequest(
    id = id,
    customerId = customerId,
    customerName = customerName,
    customerPhone = customerPhone,
    deliveryLatitude = deliveryLatitude,
    deliveryLongitude = deliveryLongitude,
    deliveryAddress = deliveryAddress,
    status = status.toMedicineRequestStatus(),
    createdAt = createdAt,
    createdAtMillis = createdAt.toEpochMillis(),
    items = items.map { it.toDomain() },
    prescriptionUrl = prescriptionUrl,
    notes = notes,
    paymentMethod = paymentMethod.toPaymentMethod(),
)

fun MedicineRequestPageDto.toDomain(): MedicineRequestPage = MedicineRequestPage(
    content = content.map(MedicineRequestDto::toDomain),
    pageNumber = pageNumber,
    totalPages = totalPages,
    last = last,
)

fun MedicineRequestItemDto.toDomain(): MedicineRequestItem = MedicineRequestItem(
    id = id,
    productId = productId,
    imageUrl = product?.imageUrl?.takeIf(String::isNotBlank),
    productName = product?.name?.ifBlank { product.productName.orEmpty() } ?: product?.productName.orEmpty(),
    strength = product?.strength,
    packSize = product?.packSize,
    form = product?.form,
    quantity = quantity?.toInt() ?: 0,
    unitPrice = unitPrice ?: product?.price ?: 0.0,
)

private fun String.toMedicineRequestStatus(): MedicineRequestStatus =
    runCatching { MedicineRequestStatus.valueOf(uppercase()) }
        .getOrDefault(MedicineRequestStatus.UNKNOWN)

private fun String?.toPaymentMethod(): PaymentMethod = when (this?.uppercase()) {
    "CASH" -> PaymentMethod.CASH
    "CARD", "VISA" -> PaymentMethod.CARD
    else -> PaymentMethod.UNKNOWN
}

private fun String.toEpochMillis(): Long =
    runCatching { OffsetDateTime.parse(this).toInstant().toEpochMilli() }
        .recoverCatching { Instant.parse(this).toEpochMilli() }
        .recoverCatching { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC).toEpochMilli() }
        .getOrDefault(0L)
