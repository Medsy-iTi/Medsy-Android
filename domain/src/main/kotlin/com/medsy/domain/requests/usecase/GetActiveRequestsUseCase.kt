package com.medsy.domain.requests.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.fold
import com.medsy.domain.common.map
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.repository.OrdersRepository
import com.medsy.domain.orders.usecase.DetermineOrderNextActionUseCase
import com.medsy.domain.requests.model.ActiveRequestsLookup
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.model.MedicineRequestStatus
import com.medsy.domain.requests.repository.RequestsRepository
import javax.inject.Inject

class GetActiveRequestsUseCase @Inject constructor(
    private val requestsRepository: RequestsRepository,
    private val ordersRepository: OrdersRepository,
    private val determineOrderNextActionUseCase: DetermineOrderNextActionUseCase,
) {
    suspend operator fun invoke(): MedsyResult<ActiveRequestsLookup, MedsyError.Remote> {
        requestsRepository.getRequests(0, PAGE_SIZE, SORT)
            .fold(
                onError = { error ->
                    return MedsyResult.Error(error)
                },
                onSuccess = { requestPage ->
                    val pendingRequests = requestPage.content.filter { it.status.isActive() }

                    loadRecentOrders().fold(
                        onError = { error -> return MedsyResult.Error(error) },
                        onSuccess = { recentOrders ->
                            return filterWithRecentOrders(pendingRequests, recentOrders)
                        }
                    )
                }
            )
    }

    private fun filterWithRecentOrders(
        pendingRequests: List<MedicineRequest>,
        recentOrders: List<MasterOrder>,
    ): MedsyResult<ActiveRequestsLookup, MedsyError.Remote> {
        val cutoff = System.currentTimeMillis() - SEARCH_DURATION_MILLIS

        val requestsWithMasterOrders =
            recentOrders.mapTo(mutableSetOf()) { it.requestId }

        val activeRequests = pendingRequests
            .filter { it.createdAtMillis > cutoff && it.id !in requestsWithMasterOrders }
            .sortedByDescending(MedicineRequest::id)

        val resumableOrder = recentOrders
            .filter { determineOrderNextActionUseCase(it) != OrderNextAction.VIEW_DETAILS }
            .maxByOrNull(MasterOrder::id)

        return MedsyResult.Success(
            ActiveRequestsLookup(
                activeRequests,
                resumableOrder
            )
        )
    }

    private suspend fun loadRecentOrders(): MedsyResult<List<MasterOrder>, MedsyError.Remote> {
        return ordersRepository.getOrders(0, PAGE_SIZE, SORT)
            .map { orderPage -> orderPage.content }
    }

    private fun MedicineRequestStatus.isActive(): Boolean = this in setOf(
        MedicineRequestStatus.PENDING,
        MedicineRequestStatus.SEARCHING,
        MedicineRequestStatus.OFFERS_READY,
    )

    private companion object {
        const val PAGE_SIZE = 3
        const val SEARCH_DURATION_MILLIS = 15 * 60 * 1_000L
        val SORT = listOf("id,desc")
    }
}
