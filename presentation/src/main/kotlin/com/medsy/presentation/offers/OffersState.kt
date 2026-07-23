package com.medsy.presentation.offers

import com.medsy.presentation.offers.model.PharmacyOffer

data class OffersState(
    val isLoading: Boolean = false,
    val availableOffers: List<PharmacyOffer> = emptyList(),
    val selectedOffer: PharmacyOffer? = null,
    val deliveryFee: Int = 20,
    val isConfirmingOrder: Boolean = false,
    val orderConfirmed: Boolean = false,
    val orderId: String? = null
)
