package com.medsy.presentation.cart

import androidx.annotation.StringRes
import com.medsy.domain.cart.model.CartDraft
import com.medsy.domain.cart.model.CartItem

data class CartState(
    val isLoading: Boolean = true,
    val items: List<CartItem> = emptyList(),
    val totalPriceEgp: Double = 0.0,
    val isRefreshing: Boolean = false,
    val draft: CartDraft = CartDraft(),
    val updatingItemIds: Set<Long> = emptySet(),
    val isClearing: Boolean = false,
    val isClearDialogVisible: Boolean = false,
    val isNoteDialogVisible: Boolean = false,
    val noteInput: String = "",
    @StringRes val errorMessageRes: Int? = null,
) {
    val hasContent: Boolean
        get() = items.isNotEmpty() ||
            draft.prescriptionImage != null ||
            draft.pharmacistNote.isNotBlank()
}
