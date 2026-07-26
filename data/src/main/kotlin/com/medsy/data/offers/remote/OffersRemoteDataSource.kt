package com.medsy.data.offers.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
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

    suspend fun acceptOffer(requestId: Long, selectedRequestItemIds: List<Long>): MedsyResult<Unit, MedsyError.Remote> {
        return safeEmptyRestCall {
            apiService.confirmRequest(requestId, ConfirmRequestDto(selectedRequestItemIds))
        }
    }
}
