package com.medsy.domain.offers.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.OffersPage

interface OffersRepository {
    suspend fun getOffersForRequest(
        requestId: Long,
        page: Int = 0,
        size: Int = 20,
    ): MedsyResult<OffersPage, MedsyError.Remote>

    suspend fun acceptOffer(
        offerId: Long,
    ): EmptyMedsyResult<MedsyError.Remote>
}
