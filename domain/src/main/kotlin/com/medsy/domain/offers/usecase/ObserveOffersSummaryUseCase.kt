package com.medsy.domain.offers.usecase
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.repository.OffersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
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
        return offersRepository.streamRequestResult(requestId).map<RequestResult, OfferSummary?> { result ->
            val totalCount = result.items.size
            val foundCount = result.items.count { it.isAvailable }
            val minPrice = result.totalPrice.toInt()

            OfferSummary(
                requestId = requestId,
                minPrice = minPrice,
                foundCount = foundCount,
                totalCount = totalCount,
                totalOffers = 1
            )
        }.catch { 
            emit(null)
        }.onStart { emit(null) }
    }
}
