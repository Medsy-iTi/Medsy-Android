package com.medsy.data.offers.repository

import com.medsy.data.offers.mapper.toDomain
import com.medsy.data.offers.remote.OffersRemoteDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.asEmptyDataResult
import com.medsy.domain.offers.model.OffersPage
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

    override suspend fun acceptOffer(
        requestId: Long,
        selectedRequestItemIds: List<Long>,
    ): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.acceptOffer(requestId, selectedRequestItemIds)
            .asEmptyDataResult()
}
