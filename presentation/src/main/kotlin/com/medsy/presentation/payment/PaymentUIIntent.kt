package com.medsy.presentation.payment

sealed interface PaymentUIIntent {
    data class StartPayment(
        val orderId: Long,
    ) : PaymentUIIntent

    data object Retry : PaymentUIIntent
}