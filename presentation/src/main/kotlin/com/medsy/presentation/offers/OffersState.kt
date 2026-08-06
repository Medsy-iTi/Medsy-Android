package com.medsy.presentation.offers

import androidx.annotation.StringRes


data class OffersState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val requestResult: com.medsy.domain.offers.model.RequestResult? = null,
    val selectedItemIds: Set<Long> = emptySet(), // Stores requestItemIds that the user selected
    val deliveryFee: Int = 20,
    val isConfirmingOrder: Boolean = false,
    val orderConfirmed: Boolean = false,
    val orderId: String? = null,
    val pharmacyName: String? = null, // Will hold the pharmacy name after confirmation
    val requestId: Long? = null
)
