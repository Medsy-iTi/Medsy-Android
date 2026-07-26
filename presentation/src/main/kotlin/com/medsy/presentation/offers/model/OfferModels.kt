package com.medsy.presentation.offers.model

enum class OfferType {
    FULL,
    PARTIAL
}

data class PharmacyOffer(
    val id: String,
    val pharmacyName: String,
    val managerName: String,
    val price: Int,
    val type: OfferType,
    val medicines: List<OfferMedicine>,
    val pharmacistComment: String? = null
)

data class OfferMedicine(
    val id: String,
    val name: String,
    val packageInfo: String,
    val price: Int,
    val isAvailable: Boolean,
    val quantity: Int = 1,
    val imageUrl: String? = null,
    val isSubstitute: Boolean = false,
    val originalProductName: String? = null
)
