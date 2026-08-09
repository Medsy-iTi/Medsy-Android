package com.medsy.presentation.offers

import androidx.annotation.StringRes


data class OffersState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val requestResult: com.medsy.domain.offers.model.RequestResult? = null,
    val selectedItemIds: Set<Long> = emptySet(), 
    val deliveryFee: Int = 20,
    val isConfirmingOrder: Boolean = false,
    val orderConfirmed: Boolean = false,
    val orderId: String? = null,
    val pharmacyName: String? = null,
    val requestId: Long? = null
)
