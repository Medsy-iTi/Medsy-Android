package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.RequestResultEvent
import com.medsy.domain.offers.repository.OffersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamRequestResultUseCase @Inject constructor(
    private val repository: OffersRepository,
) {
    operator fun invoke(requestId: Long): Flow<MedsyResult<RequestResultEvent, MedsyError.Remote>> =
        repository.streamRequestResult(requestId)
}
