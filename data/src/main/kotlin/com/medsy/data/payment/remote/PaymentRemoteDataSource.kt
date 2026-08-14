package com.medsy.data.payment.remote


import com.medsy.data.payment.remote.dto.CreatePaymentIntentRequestDto
import com.medsy.data.payment.remote.dto.CreatePaymentIntentResponseDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface PaymentRemoteDataSource {

    suspend fun createPaymentIntent(
        request: CreatePaymentIntentRequestDto,
    ): MedsyResult<CreatePaymentIntentResponseDto, MedsyError.Remote>
}