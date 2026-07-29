package com.medsy.presentation.offers

import androidx.annotation.StringRes
import com.medsy.presentation.offers.model.PharmacyOffer

data class OffersState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val availableOffers: List<PharmacyOffer> = emptyList(),
    val selectedOffer: PharmacyOffer? = null,
    val deliveryFee: Int = 20,
    val isConfirmingOrder: Boolean = false,
    val orderConfirmed: Boolean = false,
    val orderId: String? = null,
    val requestId: Long? = null
)
