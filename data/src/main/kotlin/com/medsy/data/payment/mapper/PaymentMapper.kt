package com.medsy.data.payment.mapper


import com.medsy.data.payment.remote.dto.CreatePaymentIntentResponseDto
import com.medsy.domain.payment.model.PaymentIntent

fun CreatePaymentIntentResponseDto.toDomain(): PaymentIntent {
    return PaymentIntent(
        paymentIntentId = paymentIntentId,
        clientSecret = clientSecret,
    )
}