package com.medsy.presentation.payment

sealed interface PaymentUIEffect {

    data object PaymentCompleted : PaymentUIEffect

    data class PaymentFailed(
        val messageRes: Int,
    ) : PaymentUIEffect

    data object PaymentCanceled : PaymentUIEffect

    data object NavigateBack : PaymentUIEffect
}