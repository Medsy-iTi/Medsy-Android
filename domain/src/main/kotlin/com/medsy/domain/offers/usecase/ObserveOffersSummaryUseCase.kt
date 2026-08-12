package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.RequestItemAvailability
import com.medsy.domain.offers.model.OfferSummary
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultEvent
import com.medsy.domain.offers.repository.OffersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObserveOffersSummaryUseCase @Inject constructor(
    private val offersRepository: OffersRepository,
) {
    operator fun invoke(requestId: Long): Flow<OfferSummary?> = flow {
        var currentResult: RequestResult? = null

        when (val initial = offersRepository.getRequestResult(requestId)) {
            is MedsyResult.Success -> {
                currentResult = initial.data
                emit(initial.data.toSummary())
            }
            is MedsyResult.Error -> emit(null)
        }

        offersRepository.streamRequestResult(requestId).collect { streamResult ->
            if (streamResult !is MedsyResult.Success) return@collect
            when (val event = streamResult.data) {
                is RequestResultEvent.Snapshot -> currentResult = event.result
                is RequestResultEvent.ItemsUpdated -> currentResult = currentResult?.let { result ->
                    result.copy(
                        items = result.items.map { item ->
                            val update = event.items.lastOrNull { it.requestItemId == item.requestItemId }
                                ?: return@map item
                            when (update.status) {
                                RequestItemAvailability.FOUND -> item.copy(isAvailable = true)
                                RequestItemAvailability.ALTERNATIVE_FOUND -> item.copy(
                                    isAvailable = true,
                                    alternatives = update.product
                                        ?.takeUnless { product -> item.alternatives.any { it.id == product.id } }
                                        ?.let { item.alternatives + it }
                                        ?: item.alternatives,
                                )
                                RequestItemAvailability.NOT_FOUND -> item.copy(isAvailable = false)
                                RequestItemAvailability.UNKNOWN -> item
                            }
                        }
                    )
                }
                is RequestResultEvent.Closed -> Unit
            }
            emit(currentResult?.toSummary())
        }
    }
}

private fun RequestResult.toSummary() = OfferSummary(
    foundCount = items.count { it.isAvailable },
    totalCount = items.size,
)
