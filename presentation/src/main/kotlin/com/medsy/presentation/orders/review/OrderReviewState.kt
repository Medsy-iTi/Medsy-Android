package com.medsy.presentation.orders.review

import androidx.annotation.StringRes
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.orders.model.FulfillmentMethod
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.model.PaymentStatus
import com.medsy.domain.requests.model.MedicineRequest

data class OrderReviewState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isConfirming: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val request: MedicineRequest? = null,
    val requestResult: RequestResult? = null,
    val order: MasterOrder? = null,
    val selectedItems: List<SelectedOfferItem> = emptyList(),
    val selectedFulfillment: FulfillmentMethod? = null,
    val nextAction: OrderNextAction = OrderNextAction.VIEW_DETAILS,
    val fulfillmentConfirmed: Boolean = false,
) {
    val pickupTotal: Double get() = order?.itemSubtotal ?: 0.0
    val displayedDeliveryFee: Double
        get() = if (selectedFulfillment == FulfillmentMethod.DELIVERY) order?.deliveryFee
            ?: 0.0 else 0.0
    val displayedTotal: Double
        get() = if (selectedFulfillment == FulfillmentMethod.DELIVERY) order?.totalPrice
            ?: 0.0 else pickupTotal
    val isRetryPayment: Boolean
        get() = order?.paymentStatus in setOf(PaymentStatus.FAILED, PaymentStatus.CANCELED)
}
