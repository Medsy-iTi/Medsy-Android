package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.FulfillmentConfirmation
import com.medsy.domain.offers.repository.OffersRepository
import com.medsy.domain.orders.model.FulfillmentMethod
import javax.inject.Inject

class ConfirmFulfillmentUseCase @Inject constructor(
    private val repository: OffersRepository,
) {
    suspend operator fun invoke(
        requestId: Long,
        fulfillmentMethod: FulfillmentMethod,
    ): MedsyResult<FulfillmentConfirmation, MedsyError.Remote> =
        repository.confirmFulfillment(requestId, fulfillmentMethod)
}
