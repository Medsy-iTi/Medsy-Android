package com.medsy.data.offers.repository

import com.medsy.data.offers.mapper.toDomain
import com.medsy.data.offers.remote.OffersRemoteDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.asEmptyDataResult
import com.medsy.domain.offers.model.OffersPage
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.repository.OffersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OffersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OffersRemoteDataSource,
) : OffersRepository {
    override suspend fun getOffersForRequest(
        requestId: Long,
        page: Int,
        size: Int,
    ): MedsyResult<OffersPage, MedsyError.Remote> =
        remoteDataSource.getOffersForRequest(requestId, page, size)
            .map { it.toDomain() }

    override fun observeOffersWithPolling(
        requestId: Long,
        pollIntervalMillis: Long
    ): kotlinx.coroutines.flow.Flow<MedsyResult<OffersPage, MedsyError.Remote>> = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(getOffersForRequest(requestId))
            kotlinx.coroutines.delay(pollIntervalMillis)
        }
    }

    override suspend fun acceptOffer(
        requestId: Long,
        selectedRequestItemIds: List<Long>,
    ): MedsyResult<com.medsy.domain.offers.model.ConfirmOfferResult, MedsyError.Remote> =
        remoteDataSource.acceptOffer(requestId, selectedRequestItemIds)
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
}
