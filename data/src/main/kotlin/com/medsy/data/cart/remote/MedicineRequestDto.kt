package com.medsy.data.cart.remote

import com.medsy.data.offers.remote.ResultProductDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MedicineRequestDto(
    val id: Long,
    val customerId: Long,
    val customerName: String?,
    val customerPhone: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: String,
    val createdAt: String,
    val items: List<MedicineRequestItemDto>,
    val prescriptionUrl: String?,
    val notes: String?
)

@JsonClass(generateAdapter = true)
data class MedicineRequestItemDto(
    val id: Long,
    val productId: Int,
    val product: ResultProductDto?,
    val quantity: Int,
    val unitPrice: Double
)
