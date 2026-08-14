package com.medsy.domain.payment.repository


import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.payment.model.PaymentIntent

interface PaymentRepository {

    suspend fun createPaymentIntent(
        orderId: Long,
    ): MedsyResult<PaymentIntent, MedsyError.Remote>
}