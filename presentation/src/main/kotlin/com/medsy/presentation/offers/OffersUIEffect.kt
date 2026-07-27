package com.medsy.presentation.offers

sealed interface OffersUIEffect {
    data object NavigateToOfferDetails : OffersUIEffect
    data object NavigateToOrderReview : OffersUIEffect
    data class NavigateToOrderConfirmation(val orderId: String, val pharmacyName: String) : OffersUIEffect
    data object NavigateBack : OffersUIEffect
    data object NavigateToHome : OffersUIEffect
    data object NavigateToTrackOrder : OffersUIEffect
}
