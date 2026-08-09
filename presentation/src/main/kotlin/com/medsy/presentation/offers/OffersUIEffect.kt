package com.medsy.presentation.offers

import androidx.annotation.StringRes

sealed interface OffersUIEffect {
    data class NavigateToOrderReview(val selectedItemIds: Set<Long>) : OffersUIEffect
    data class NavigateToOrderConfirmation(
        val orderId: String,
        val pharmacyName: String
    ) : OffersUIEffect
    data object NavigateToTrackOrder : OffersUIEffect
    data object NavigateToHome : OffersUIEffect
    data object NavigateBack : OffersUIEffect
    data class ShowError(@StringRes val messageRes: Int) : OffersUIEffect
}
