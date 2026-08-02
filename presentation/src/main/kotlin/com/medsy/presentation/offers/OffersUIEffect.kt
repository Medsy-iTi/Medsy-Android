package com.medsy.presentation.offers

import androidx.annotation.StringRes

sealed interface OffersUIEffect {
    data object NavigateToOfferDetails : OffersUIEffect
    data object NavigateToOrderReview : OffersUIEffect
    data class NavigateToOrderConfirmation(val orderId: String, val pharmacyName: String) : OffersUIEffect
    data object NavigateBack : OffersUIEffect
    data object NavigateToHome : OffersUIEffect
    data object NavigateToTrackOrder : OffersUIEffect
    data class ShowError(@StringRes val messageRes: Int) : OffersUIEffect
}
