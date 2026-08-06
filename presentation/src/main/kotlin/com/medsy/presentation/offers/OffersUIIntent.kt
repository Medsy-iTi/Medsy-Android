package com.medsy.presentation.offers

sealed interface OffersUIIntent {
    data class LoadOffers(val requestId: Long) : OffersUIIntent
    data class ToggleItemSelection(val requestItemId: Long) : OffersUIIntent
    data object ProceedToReview : OffersUIIntent
    data object ConfirmOrder : OffersUIIntent
    data object TrackOrder : OffersUIIntent
    data object BackToHome : OffersUIIntent
    data object NavigateBack : OffersUIIntent
}
