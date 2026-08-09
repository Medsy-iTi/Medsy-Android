package com.medsy.domain.offers.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.ConfirmOfferResult
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.offers.repository.OffersRepository
import javax.inject.Inject

class AcceptOfferUseCase @Inject constructor(
    private val repository: OffersRepository,
) {
    suspend operator fun invoke(
        requestId: Long,
        selectedItems: List<SelectedOfferItem>,
    ): MedsyResult<ConfirmOfferResult, MedsyError.Remote> = repository.acceptOffer(requestId, selectedItems)
}
