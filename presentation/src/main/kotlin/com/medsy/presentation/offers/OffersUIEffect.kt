package com.medsy.presentation.offers

import androidx.annotation.StringRes
import com.medsy.domain.offers.model.SelectedOfferItem

sealed interface OffersUIEffect {
    data class NavigateToOrderReview(
        val masterOrderId: Long,
        val selectedItems: List<SelectedOfferItem>,
    ) : OffersUIEffect

    data object NavigateBack : OffersUIEffect
    data class ShowError(@StringRes val messageRes: Int) : OffersUIEffect
}
