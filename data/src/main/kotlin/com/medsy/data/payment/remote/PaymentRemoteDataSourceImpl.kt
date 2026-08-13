package com.medsy.data.payment.remote

import com.medsy.data.payment.remote.dto.CreatePaymentIntentRequestDto
import com.medsy.data.payment.remote.dto.CreatePaymentIntentResponseDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class PaymentRemoteDataSourceImpl @Inject constructor(
    private val apiService: PaymentApiService,
) : PaymentRemoteDataSource {

    override suspend fun createPaymentIntent(
        request: CreatePaymentIntentRequestDto,
    ): MedsyResult<CreatePaymentIntentResponseDto, MedsyError.Remote> {
        return safeApiCall {
            apiService.createPaymentIntent(request)
        }
    }
}