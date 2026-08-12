package com.medsy.domain.requests.usecase

import com.medsy.domain.offers.usecase.ObserveOffersSummaryUseCase
import com.medsy.domain.requests.model.ActiveRequestStatus
import com.medsy.domain.requests.model.MedicineRequest
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transformWhile

class ObserveActiveRequestsWithStatusUseCase @Inject constructor(
    private val observeOffersSummary: ObserveOffersSummaryUseCase,
) {
    operator fun invoke(requests: List<MedicineRequest>): Flow<List<ActiveRequestStatus>> {
        if (requests.isEmpty()) return flowOf(emptyList())

        val statusFlows = requests.map { request ->
            combine(
                remainingTime(request),
                observeOffersSummary(request.id),
            ) { seconds, summary ->
                ActiveRequestStatus(
                    requestId = request.id,
                    remainingTimeSeconds = seconds,
                    foundCount = summary?.foundCount ?: 0,
                    totalCount = summary?.totalCount ?: request.items.size,
                )
            }.transformWhile { status ->
                emit(status)
                status.remainingTimeSeconds > 0
            }
        }

        return combine(statusFlows) { statuses ->
            statuses.filter { it.remainingTimeSeconds > 0 }
                .sortedByDescending(ActiveRequestStatus::requestId)
        }
    }

    private fun remainingTime(request: MedicineRequest): Flow<Int> = flow {
        while (true) {
            val elapsedSeconds = ((System.currentTimeMillis() - request.createdAtMillis) / 1_000L)
                .coerceAtLeast(0L)
            val remaining = (SEARCH_DURATION_SECONDS - elapsedSeconds).coerceAtLeast(0).toInt()
            emit(remaining)
            if (remaining == 0) break
            delay(1_000L)
        }
    }

    private companion object {
        const val SEARCH_DURATION_SECONDS = 15 * 60L
    }
}
