package com.medsy.presentation.offers

sealed interface OffersUIIntent {
    data object RefreshOffers : OffersUIIntent
    data class SelectOffer(val offerId: String) : OffersUIIntent
    data object ChooseSelectedOffer : OffersUIIntent
    data object ConfirmOrder : OffersUIIntent
    data object TrackOrder : OffersUIIntent
    data object BackToHome : OffersUIIntent
    data object NavigateBack : OffersUIIntent
}
