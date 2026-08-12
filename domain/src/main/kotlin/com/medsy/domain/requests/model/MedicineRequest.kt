package com.medsy.domain.requests.model

import com.medsy.domain.common.model.PaymentMethod

data class MedicineRequest(
    val id: Long,
    val customerId: Long,
    val customerName: String?,
    val customerPhone: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: MedicineRequestStatus,
    val createdAt: String,
    val createdAtMillis: Long,
    val items: List<MedicineRequestItem>,
    val prescriptionUrl: String?,
    val notes: String?,
    val paymentMethod: PaymentMethod,
)

data class MedicineRequestItem(
    val id: Long,
    val productId: Long?,
    val imageUrl: String?,
    val productName: String,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val quantity: Int,
    val unitPrice: Double,
)

enum class MedicineRequestStatus {
    SEARCHING,
    OFFERS_READY,
    PENDING,
    COMPLETED,
    CANCELLED,
    EXPIRED,
    UNKNOWN,
}
