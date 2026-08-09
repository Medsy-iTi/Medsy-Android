package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.repository.OffersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

data class OfferSummary(
    val requestId: Long,
    val minPrice: Int,
    val foundCount: Int,
    val totalCount: Int,
    val totalOffers: Int,
)

class ObserveOffersSummaryUseCase @Inject constructor(
    private val offersRepository: OffersRepository,
) {
    operator fun invoke(requestId: Long): Flow<OfferSummary?> {
        return offersRepository.streamRequestResult(requestId)
            .map { result ->
                if (result.items.any { it.isAvailable }) {
                    result.toSummary(requestId)
                } else {
                    null
                }
            }
            .catch {
                emit(null)
            }
            .onStart {
                val snapshot = offersRepository.getRequestResult(requestId)
                if (snapshot is MedsyResult.Success && snapshot.data.items.any { it.isAvailable }) {
                    emit(snapshot.data.toSummary(requestId))
                } else {
                    emit(null)
                }
            }
    }
}

private fun RequestResult.toSummary(requestId: Long) = OfferSummary(
    requestId = requestId,
    minPrice = totalPrice.toInt(),
    foundCount = items.count { it.isAvailable },
    totalCount = items.size,
    totalOffers = 1,
)
