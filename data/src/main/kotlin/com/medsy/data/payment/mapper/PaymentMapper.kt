package com.medsy.data.payment.mapper


import com.medsy.data.payment.dtos.CreatePaymentIntentResponseDto
import com.medsy.domain.payment.model.PaymentIntentDomain

fun CreatePaymentIntentResponseDto.toDomain() =
    PaymentIntentDomain(
        clientSecret = clientSecret,
        paymentIntentId = paymentIntentId
    )