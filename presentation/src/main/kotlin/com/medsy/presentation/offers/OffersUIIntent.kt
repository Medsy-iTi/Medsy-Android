package com.medsy.presentation.offers

sealed interface OffersUIIntent {
    data class LoadOffers(val requestId: Long) : OffersUIIntent
    data class LoadOfferDetails(val requestId: Long, val offerId: String) : OffersUIIntent
    data class SelectOffer(val offerId: String) : OffersUIIntent
    data object ChooseSelectedOffer : OffersUIIntent
    data object ConfirmOrder : OffersUIIntent
    data object PaymentSuccess : OffersUIIntent
    data class PaymentFailed(val error: String?) : OffersUIIntent
    data object PaymentCanceled : OffersUIIntent
    data object TrackOrder : OffersUIIntent
    data object BackToHome : OffersUIIntent
    data object NavigateBack : OffersUIIntent
}
