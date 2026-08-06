package com.medsy.domain.offers.usecase

import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offers.repository.OffersRepository
import com.medsy.domain.requests.usecase.GetMedicineRequestByIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        return offersRepository.streamRequestResult(requestId).map { result ->
            val totalCount = result.items.size
            val foundCount = result.items.count { it.isAvailable }
            val minPrice = result.totalPrice.toInt()

            OfferSummary(
                requestId = requestId,
                minPrice = minPrice,
                foundCount = foundCount,
                totalCount = totalCount,
                totalOffers = 1 // Aggregated by backend
            )
        }
    }
}
