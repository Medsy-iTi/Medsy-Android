package com.medsy.data.payment.repsitory


import com.medsy.data.payment.remote.dto.CreatePaymentIntentRequestDto
import com.medsy.data.payment.mapper.toDomain
import com.medsy.data.payment.remote.PaymentRemoteDataSource
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.payment.model.PaymentIntent
import com.medsy.domain.payment.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
) : PaymentRepository {

    override suspend fun createPaymentIntent(
        orderId: Long,
    ): MedsyResult<PaymentIntent, MedsyError.Remote> {

        return when (
            val result = remoteDataSource.createPaymentIntent(
                CreatePaymentIntentRequestDto(
                    masterOrderId = orderId,
                )
            )
        ) {
            is MedsyResult.Success -> {
                MedsyResult.Success(result.data.toDomain())
            }

            is MedsyResult.Error -> {
                result
            }
        }
    }
}