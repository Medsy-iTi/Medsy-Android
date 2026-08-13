package com.medsy.presentation.offers

import androidx.annotation.StringRes
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.requests.model.MedicineRequest

data class OffersState(
    val requestId: Long? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val request: MedicineRequest? = null,
    val requestResult: RequestResult? = null,
    val selectedItems: List<SelectedOfferItem> = emptyList(),
    val manuallyDeselectedRequestItemIds: Set<Long> = emptySet(),
    val selectionSubmitted: Boolean = false,
)
