package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SelectionResponseDto(
    val requestId: Long,
    val offers: List<PharmacyAllocationDto>,
    val deliveryFees: Double,
    val totalPrice: Double,
)

@JsonClass(generateAdapter = true)
data class PharmacyAllocationDto(
    val offerId: Long,
    val pharmacyId: Long,
    val pharmacyName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val items: List<AllocatedOrderItemDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class AllocatedOrderItemDto(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val product: RequestResultProductDto? = null,
)

@JsonClass(generateAdapter = true)
data class FulfillmentRequestDto(
    val fulfillmentMethod: String,
)

@JsonClass(generateAdapter = true)
data class FulfillmentConfirmationDto(
    val masterOrderId: Long,
    val orderStatus: String,
    val paymentMethod: String,
    val paymentStatus: String? = null,
)
