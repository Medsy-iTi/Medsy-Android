package com.medsy.data.orders.model

import com.medsy.data.offers.remote.PharmacyAllocationDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MasterOrderPageDto(
    val content: List<MasterOrderDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
)

@JsonClass(generateAdapter = true)
data class MasterOrderDto(
    val id: Long,
    val requestId: Long,
    val orderResponses: List<PharmacyAllocationDto> = emptyList(),
    val paymentMethod: String,
    val paymentStatus: String? = null,
    val fulfillmentMethod: String? = null,
    val deliveryFee: Double? = null,
    val totalPrice: Double,
    val orderStatus: String,
    val paymentExpiresAt: String? = null,
    val paidAt: String? = null,
)
