package com.medsy.presentation.orders.review

sealed interface OrderReviewUIEffect {

    data object NavigateBack : OrderReviewUIEffect

    data class StartCardPayment(
        val orderId: Long,
    ) : OrderReviewUIEffect

    data class NavigateToOrderDetails(
        val masterOrderId: Long,
    ) : OrderReviewUIEffect

    data class NavigateToPharmacyProfile(
        val pharmacyId: Long,
    ) : OrderReviewUIEffect

    data class ShowError(
        val messageRes: Int,
    ) : OrderReviewUIEffect
}