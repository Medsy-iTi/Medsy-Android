package com.medsy.domain.offers.model

data class Offer(
    val id: Long,
    val requestId: Long,
    val pharmacyId: Long,
    val pharmacistId: Long,
    val pharmacyName: String?,
    val pharmacistName: String?,
    val status: String,
    val distanceKm: Double?,
    val items: List<OfferItem>
)

data class OfferItem(
    val id: Long,
    val requestItemId: Long,
    val productId: Int
)

data class OffersPage(
    val content: List<Offer>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)
