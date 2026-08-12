package com.medsy.presentation.offers

sealed interface OffersUIIntent {
    data class LoadOfferDetails(val requestId: Long) : OffersUIIntent
    data class SelectProduct(val requestItemId: Long, val productId: Long?) : OffersUIIntent
    data object ProceedToReview : OffersUIIntent
    data object Retry : OffersUIIntent
    data object NavigateBack : OffersUIIntent
}
