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
    private val getMedicineRequestByIdUseCase: GetMedicineRequestByIdUseCase,
) {
    operator fun invoke(requestId: Long): Flow<OfferSummary?> {
        return offersRepository.observeOffersWithPolling(requestId).map { result ->
            if (result is MedsyResult.Success) {
                val offers = result.data.content
                if (offers.isNotEmpty()) {
                    val reqResult = getMedicineRequestByIdUseCase(requestId)
                    var totalCount = 0
                    var originalItemsMap = mapOf<Long, com.medsy.domain.requests.model.MedicineRequestItem>()
                    if (reqResult is MedsyResult.Success) {
                        totalCount = reqResult.data.items.size
                        originalItemsMap = reqResult.data.items.associateBy { it.id }
                    }
                    val maxFoundCount = offers.maxOfOrNull { it.items.size } ?: 0
                    val minPrice = offers.minOfOrNull { offer ->
                        offer.items.sumOf { offerItem ->
                            val reqItem = originalItemsMap[offerItem.requestItemId]
                            (reqItem?.unitPrice ?: 0.0) * (reqItem?.quantity ?: 1)
                        }.toInt()
                    } ?: 0

                    return@map OfferSummary(
                        requestId = requestId,
                        minPrice = minPrice,
                        foundCount = maxFoundCount,
                        totalCount = totalCount,
                        totalOffers = offers.size
                    )
                }
            }
            null
        }
    }
}
