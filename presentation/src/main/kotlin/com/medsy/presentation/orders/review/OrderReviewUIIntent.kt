package com.medsy.presentation.orders.review

import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.orders.model.FulfillmentMethod

sealed interface OrderReviewUIIntent {
    data class Load(
        val requestId: Long,
        val masterOrderId: Long,
        val selectedItems: List<SelectedOfferItem>,
    ) : OrderReviewUIIntent

    data class FulfillmentChanged(val method: FulfillmentMethod) : OrderReviewUIIntent
    data class PharmacyClicked(val pharmacyId: Long) : OrderReviewUIIntent
    data object ConfirmFulfillment : OrderReviewUIIntent
    data object PayClicked : OrderReviewUIIntent
    data object Retry : OrderReviewUIIntent
    data object Refresh : OrderReviewUIIntent
    data object NavigateBack : OrderReviewUIIntent
}
