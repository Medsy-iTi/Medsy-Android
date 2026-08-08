package com.medsy.domain.requests.model

data class MedicineRequestDetails(
    val id: Long,
    val customerId: Long,
    val customerName: String?,
    val customerPhone: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: String,
    val createdAt: String,
    val items: List<MedicineRequestItem>,
    val prescriptionUrl: String?,
    val notes: String?,
    val paymentMethod: String?,
)

data class MedicineRequestItem(
    val id: Long,
    val productId: Int,
    val imageUrl: String?,
    val productName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val quantity: Int,
    val unitPrice: Double
)
