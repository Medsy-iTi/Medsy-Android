package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.OffersPage
import com.medsy.domain.offers.repository.OffersRepository
import javax.inject.Inject

class GetOffersForRequestUseCase @Inject constructor(
    private val repository: OffersRepository,
) {
    suspend operator fun invoke(
        requestId: Long,
        page: Int = 0,
        size: Int = 20,
    ): MedsyResult<OffersPage, MedsyError.Remote> =
        repository.getOffersForRequest(requestId, page, size)
}
