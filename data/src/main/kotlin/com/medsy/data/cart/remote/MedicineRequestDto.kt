package com.medsy.data.cart.remote

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
    val notes: String?,
    val paymentMethod: String?,
)

@JsonClass(generateAdapter = true)
data class MedicineRequestItemDto(
    val id: Long,
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double,
    val product: MedicineRequestProductDto?,
)

@JsonClass(generateAdapter = true)
data class MedicineRequestProductDto(
    val id: Long,
    val name: String,
    val productName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val price: Double,
    val scientificName: String,
    val company: String?,
    val route: String?,
    val description: String?,
    val imageUrl: String?,
)
