package com.medsy.domain.offers.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.OffersPage
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.SelectedOfferItem
import kotlinx.coroutines.flow.Flow

interface OffersRepository {
    suspend fun getOffersForRequest(
        requestId: Long,
        page: Int = 0,
        size: Int = 20,
    ): MedsyResult<OffersPage, MedsyError.Remote>

    fun observeOffersWithPolling(
        requestId: Long,
        pollIntervalMillis: Long = 5000
    ): Flow<MedsyResult<OffersPage, MedsyError.Remote>>

    suspend fun acceptOffer(
        requestId: Long,
        selectedItems: List<SelectedOfferItem>,
    ): MedsyResult<com.medsy.domain.offers.model.ConfirmOfferResult, MedsyError.Remote>

    suspend fun getRequestResult(
        requestId: Long,
    ): MedsyResult<RequestResult, MedsyError.Remote>
}
