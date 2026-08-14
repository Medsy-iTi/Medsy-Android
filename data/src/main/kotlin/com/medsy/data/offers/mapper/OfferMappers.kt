package com.medsy.data.offers.mapper

import com.medsy.data.offers.remote.RequestResultDto
import com.medsy.data.offers.remote.RequestResultItemDto
import com.medsy.data.offers.remote.RequestStreamEventDto
import com.medsy.data.orders.mapper.toDomain
import com.medsy.data.orders.mapper.toOrderStatus
import com.medsy.data.orders.mapper.toPaymentStatus
import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.offers.model.FulfillmentConfirmation
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultEvent
import com.medsy.domain.offers.model.RequestResultItem
import com.medsy.domain.offers.model.RequestItemAvailability
import com.medsy.domain.offers.model.RequestItemUpdate
import com.medsy.domain.offers.model.ResultPharmacy
import com.medsy.domain.offers.model.SelectionDraft
import com.medsy.domain.offers.model.StreamCloseReason

fun RequestResultDto.toDomain(): RequestResult = RequestResult(
    items = medicineRequestResultItemList.map { it.toDomain() },
    totalPrice = totalPrice,
    paymentMethod = when (paymentMethod?.uppercase()) {
        "CASH" -> PaymentMethod.CASH
        "CARD", "VISA" -> PaymentMethod.CARD
        else -> PaymentMethod.UNKNOWN
    },
)

fun RequestResultItemDto.toDomain(): RequestResultItem = RequestResultItem(
    requestItemId = requestItemId,
    productId = productId,
    unitPrice = unitPrice,
    isAlternative = alternative,
    isAvailable = available,
    product = product?.toDomain(),
    alternatives = alternatives.map { it.toDomain() },
    pharmacy = pharmacy?.let { ResultPharmacy(it.id, it.name) },
)

fun com.medsy.data.offers.remote.RequestResultProductDto.toDomain(): com.medsy.domain.offers.model.ResultProduct = com.medsy.domain.offers.model.ResultProduct(
    id = id,
    name = name,
    productName = productName ?: name,
    strength = strength,
    packSize = packSize,
    form = form,
    price = price,
    scientificName = scientificName,
    company = company,
    route = route,
    description = description,
    imageUrl = imageUrl
)

fun com.medsy.data.offers.remote.SelectionResponseDto.toDomain(): SelectionDraft = SelectionDraft(
    requestId = requestId,
    pharmacyAllocations = offers.map { it.toDomain() },
    deliveryFees = deliveryFees,
    totalPrice = totalPrice,
)

fun com.medsy.data.offers.remote.FulfillmentConfirmationDto.toDomain(): FulfillmentConfirmation =
    FulfillmentConfirmation(
        masterOrderId = masterOrderId,
        orderStatus = orderStatus.toDomainOrderStatus(),
        paymentMethod = when (paymentMethod.uppercase()) {
            "CASH" -> PaymentMethod.CASH
            "CARD", "VISA" -> PaymentMethod.CARD
            else -> PaymentMethod.UNKNOWN
        },
        paymentStatus = paymentStatus?.toDomainPaymentStatus(),
    )

fun RequestStreamEventDto.toDomain(): RequestResultEvent = when (this) {
    is RequestStreamEventDto.Snapshot -> RequestResultEvent.Snapshot(result.toDomain())
    is RequestStreamEventDto.ItemsUpdated -> RequestResultEvent.ItemsUpdated(
        requestId = update.requestId,
        items = update.updatedItems.map { item ->
            RequestItemUpdate(
                requestItemId = item.requestItemId,
                status = runCatching { RequestItemAvailability.valueOf(item.status.uppercase()) }
                    .getOrDefault(RequestItemAvailability.UNKNOWN),
                product = item.product?.toDomain(),
            )
        },
    )
    is RequestStreamEventDto.Closed -> RequestResultEvent.Closed(
        when (reason.lowercase()) {
            "confirmed" -> StreamCloseReason.CONFIRMED
            "timeout" -> StreamCloseReason.TIMEOUT
            "error" -> StreamCloseReason.ERROR
            else -> StreamCloseReason.UNKNOWN
        }
    )
}

private fun String.toDomainOrderStatus() = toOrderStatus()

private fun String.toDomainPaymentStatus() = toPaymentStatus()
