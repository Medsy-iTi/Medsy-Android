package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.repository.OffersRepository
import javax.inject.Inject

class GetRequestResultUseCase @Inject constructor(
    private val repository: OffersRepository,
) {
    suspend operator fun invoke(requestId: Long): MedsyResult<RequestResult, MedsyError.Remote> =
        repository.getRequestResult(requestId)
}
