package com.medsy.domain.offers.model

data class ConfirmOfferResult(
    val requestId: Long,
    val orders: List<PharmacyOrder>
)

data class PharmacyOrder(
    val orderId: Long,
    val pharmacyId: Long,
    val pharmacyName: String?,
    val itemIds: List<Long>
)
