package com.medsy.data.payment.repository


import com.medsy.data.payment.mapper.toDomain
import com.medsy.data.payment.remote.PaymentRemoteDataSource
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.payment.model.PaymentIntentDomain
import com.medsy.domain.payment.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
) : PaymentRepository {

    override suspend fun createPaymentIntent(
        orderId: Long,
    ): MedsyResult<PaymentIntentDomain, MedsyError.Remote> =
        remoteDataSource
            .createPaymentIntent(orderId)
            .map { it.toDomain() }
}