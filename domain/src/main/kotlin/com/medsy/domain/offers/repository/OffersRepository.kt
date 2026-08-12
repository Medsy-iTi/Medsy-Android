package com.medsy.domain.offers.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.FulfillmentConfirmation
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultEvent
import com.medsy.domain.offers.model.SelectionDraft
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.orders.model.FulfillmentMethod
import kotlinx.coroutines.flow.Flow

interface OffersRepository {
    suspend fun selectItems(
        requestId: Long,
        selectedItems: List<SelectedOfferItem>,
    ): MedsyResult<SelectionDraft, MedsyError.Remote>

    suspend fun confirmFulfillment(
        requestId: Long,
        fulfillmentMethod: FulfillmentMethod,
    ): MedsyResult<FulfillmentConfirmation, MedsyError.Remote>

    suspend fun getRequestResult(
        requestId: Long,
    ): MedsyResult<RequestResult, MedsyError.Remote>

    fun streamRequestResult(
        requestId: Long,
    ): Flow<MedsyResult<RequestResultEvent, MedsyError.Remote>>
}
