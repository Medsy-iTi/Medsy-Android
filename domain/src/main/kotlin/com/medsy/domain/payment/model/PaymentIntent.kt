package com.medsy.domain.payment.model


data class PaymentIntent(
    val paymentIntentId: String,
    val clientSecret: String,
)