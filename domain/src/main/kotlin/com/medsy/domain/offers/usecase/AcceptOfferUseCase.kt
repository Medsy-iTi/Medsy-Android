package com.medsy.domain.offers.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.offers.repository.OffersRepository
import javax.inject.Inject

class AcceptOfferUseCase @Inject constructor(
    private val repository: OffersRepository,
) {
    suspend operator fun invoke(
        offerId: Long,
    ): EmptyMedsyResult<MedsyError.Remote> = repository.acceptOffer(offerId)
}
