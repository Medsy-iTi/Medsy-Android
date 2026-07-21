package com.medsy.presentation.orders.details

import androidx.annotation.StringRes
import com.medsy.presentation.orders.details.model.OrderDetails


data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val order: OrderDetails? = null,
    val errorMessageRes: Int? = null,
)
