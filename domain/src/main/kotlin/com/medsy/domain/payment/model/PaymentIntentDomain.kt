package com.medsy.domain.payment.model


data class PaymentIntentDomain(
    val clientSecret: String,
    val paymentIntentId: String

)