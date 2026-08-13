package com.medsy.presentation.payment

data class PaymentState(
    val isLoading: Boolean = false,
    val clientSecret: String? = null,
    val errorMessageRes: Int? = null,
)