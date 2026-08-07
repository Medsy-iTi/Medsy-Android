package com.medsy.domain.payment.usecase


import com.medsy.domain.payment.repository.PaymentRepository
import javax.inject.Inject

class CreatePaymentIntentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {

    suspend operator fun invoke(
        orderId: Long
    ) = repository.createPaymentIntent(orderId)

}