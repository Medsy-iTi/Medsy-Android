package com.medsy.domain.offers.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.SelectedOfferItem
import kotlinx.coroutines.flow.Flow

interface OffersRepository {
    suspend fun acceptOffer(
        requestId: Long,
        selectedItems: List<SelectedOfferItem>,
    ): MedsyResult<com.medsy.domain.offers.model.ConfirmOfferResult, MedsyError.Remote>

    suspend fun getRequestResult(
        requestId: Long,
    ): MedsyResult<RequestResult, MedsyError.Remote>

    fun streamRequestResult(
        requestId: Long,
    ): Flow<RequestResult>
}
