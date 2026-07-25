package com.medsy.data.offers.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class OffersRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getOffersForRequest(
        requestId: Long,
        page: Int = 0,
        size: Int = 20,
    ): MedsyResult<OffersPageDto, MedsyError.Remote> =
        safeApiCall {
            apiService.getOffersForRequest(requestId, page, size)
        }

    suspend fun acceptOffer(
        offerId: Long,
    ): MedsyResult<Any, MedsyError.Remote> =
        safeApiCall {
            apiService.acceptOffer(offerId)
        }
}
