package com.medsy.domain.requests.usecase

import com.medsy.domain.offers.usecase.ObserveOffersSummaryUseCase
import com.medsy.domain.requests.model.ActiveRequestStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveActiveRequestsWithStatusUseCase @Inject constructor(
    private val observeActiveRequestsUseCase: ObserveActiveRequestsUseCase,
    private val removeActiveRequestUseCase: RemoveActiveRequestUseCase,
    private val observeOffersSummaryUseCase: ObserveOffersSummaryUseCase
) {
    operator fun invoke(): Flow<List<ActiveRequestStatus>> {
        return observeActiveRequestsUseCase().flatMapLatest { requests ->
            if (requests.isEmpty()) {
                flowOf(emptyList())
            } else {
                val statusFlows = requests.map { request ->
                    val tickFlow = flow {
                        while (true) {
                            val elapsedMillis = System.currentTimeMillis() - request.createdAtMillis
                            val remainingSeconds = (900 - (elapsedMillis / 1000)).toInt()
                            if (remainingSeconds <= 0) {
                                removeActiveRequestUseCase(request.id)
                                break
                            }
                            emit(remainingSeconds)
                            delay(1000)
                        }
                    }

                    combine(tickFlow, observeOffersSummaryUseCase(request.id)) { seconds, summary ->
                        if (summary == null) {
                            ActiveRequestStatus.Searching(request.id, seconds)
                        } else if (summary.totalOffers == 1) {
                            ActiveRequestStatus.FirstOfferArrived(
                                requestId = request.id,
                                remainingTimeSeconds = seconds,
                                minPrice = summary.minPrice,
                                foundCount = summary.foundCount,
                                totalCount = summary.totalCount
                            )
                        } else {
                            ActiveRequestStatus.MultipleOffersArrived(
                                requestId = request.id,
                                remainingTimeSeconds = seconds,
                                minPrice = summary.minPrice,
                                totalOffers = summary.totalOffers,
                                foundCount = summary.foundCount,
                                totalCount = summary.totalCount
                            )
                        }
                    }
                }

                combine(statusFlows) { statuses ->
                    statuses.toList().sortedWith(
                        compareByDescending<ActiveRequestStatus> { 
                            it is ActiveRequestStatus.FirstOfferArrived || it is ActiveRequestStatus.MultipleOffersArrived
                        }.thenByDescending { it.requestId }
                    )
                }
            }
        }
    }
}
