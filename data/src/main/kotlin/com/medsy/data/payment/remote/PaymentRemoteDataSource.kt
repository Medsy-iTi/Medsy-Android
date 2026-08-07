package com.medsy.data.payment.remote


import com.medsy.data.payment.dtos.CreatePaymentIntentRequestDto
import com.medsy.data.payment.dtos.CreatePaymentIntentResponseDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class PaymentRemoteDataSource @Inject constructor(
    private val paymentApi: PaymentApi,
) {

    suspend fun createPaymentIntent(
        orderId: Long,
    ): MedsyResult<CreatePaymentIntentResponseDto, MedsyError.Remote> =
        safeApiCall {
            paymentApi.createPaymentIntent(
                CreatePaymentIntentRequestDto(orderId)
            )
        }
}