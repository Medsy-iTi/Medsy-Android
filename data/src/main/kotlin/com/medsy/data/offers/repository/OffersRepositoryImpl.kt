package com.medsy.data.offers.repository

import com.medsy.data.offers.mapper.toDomain
import com.medsy.data.offers.remote.OffersRemoteDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.asEmptyDataResult
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.repository.OffersRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OffersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OffersRemoteDataSource,
) : OffersRepository {
    override suspend fun acceptOffer(
        requestId: Long,
        selectedItems: List<com.medsy.domain.offers.model.SelectedRequestItem>,
    ): MedsyResult<com.medsy.domain.offers.model.ConfirmOfferResult, MedsyError.Remote> =
        remoteDataSource.acceptOffer(requestId, selectedItems.map { com.medsy.data.offers.remote.SelectedRequestItemDto(it.requestItemId, it.productId) })
            .map { dto ->
                com.medsy.domain.offers.model.ConfirmOfferResult(
                    requestId = dto.requestId,
                    orders = dto.orders.map { orderDto ->
                        com.medsy.domain.offers.model.PharmacyOrder(
                            orderId = orderDto.orderId,
                            pharmacyId = orderDto.pharmacyId,
                            pharmacyName = orderDto.pharmacyName,
                            itemIds = orderDto.itemIds
                        )
                    }
                )
            }

    override suspend fun getRequestResult(
        requestId: Long,
    ): MedsyResult<RequestResult, MedsyError.Remote> =
        remoteDataSource.getRequestResult(requestId).map { it.toDomain() }

    override fun streamRequestResult(
        requestId: Long,
    ): kotlinx.coroutines.flow.Flow<RequestResult> =
        remoteDataSource.streamRequestResult(requestId)
            .retryWhen { cause, attempt ->
                // Retry on any IOException (network drop, SSE failure, server-side close).
                // CancellationException is never an IOException so coroutine lifecycle is safe.
                if (cause is IOException && attempt < 5) {
                    // Exponential backoff: 2s, 4s, 8s, 16s, 30s
                    val delayMs = minOf(2_000L * (1L shl attempt.toInt()), 30_000L)
                    delay(delayMs)
                    true
                } else {
                    false
                }
            }
            .map { it.toDomain() }
}
