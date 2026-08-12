package com.medsy.presentation.orders.review

import androidx.annotation.StringRes

sealed interface OrderReviewUIEffect {
    data object NavigateBack : OrderReviewUIEffect
    data class NavigateToOrderDetails(val masterOrderId: Long) : OrderReviewUIEffect
    data class NavigateToPharmacyProfile(val pharmacyId: Long) : OrderReviewUIEffect
    data class StartCardPayment(val masterOrderId: Long) : OrderReviewUIEffect
    data class ShowError(@StringRes val messageRes: Int) : OrderReviewUIEffect
}
