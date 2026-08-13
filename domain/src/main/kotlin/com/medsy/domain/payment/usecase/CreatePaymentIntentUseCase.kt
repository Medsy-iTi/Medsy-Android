package com.medsy.domain.payment.usecase


import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.payment.model.PaymentIntent
import com.medsy.domain.payment.repository.PaymentRepository
import javax.inject.Inject

class CreatePaymentIntentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
) {

    suspend operator fun invoke(
        orderId: Long,
    ): MedsyResult<PaymentIntent, MedsyError.Remote> {
        return paymentRepository.createPaymentIntent(orderId)
    }
}