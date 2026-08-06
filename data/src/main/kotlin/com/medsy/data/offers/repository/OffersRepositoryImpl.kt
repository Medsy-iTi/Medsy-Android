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
import kotlinx.coroutines.flow.map
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
        remoteDataSource.streamRequestResult(requestId).map { it.toDomain() }
}
