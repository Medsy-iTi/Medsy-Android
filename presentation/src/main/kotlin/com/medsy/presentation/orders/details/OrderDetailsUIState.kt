package com.medsy.presentation.orders.details

import androidx.annotation.StringRes
import com.medsy.domain.orders.model.MasterOrder

data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isReordering: Boolean = false,
    val order: MasterOrder? = null,
    @StringRes val errorMessageRes: Int? = null,
)
