package com.medsy.presentation.offers

sealed interface OffersUIEffect {
    data object NavigateToOfferDetails : OffersUIEffect
    data object NavigateToOrderReview : OffersUIEffect
    data object NavigateToOrderConfirmation : OffersUIEffect
    data object NavigateBack : OffersUIEffect
    data object NavigateToHome : OffersUIEffect
    data object NavigateToTrackOrder : OffersUIEffect
}
