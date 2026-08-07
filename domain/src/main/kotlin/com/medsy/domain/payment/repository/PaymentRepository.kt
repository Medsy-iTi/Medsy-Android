package com.medsy.domain.payment.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.payment.model.PaymentIntentDomain


interface PaymentRepository {

    suspend fun createPaymentIntent(
        orderId: Long
    ): MedsyResult<PaymentIntentDomain, MedsyError.Remote>

}