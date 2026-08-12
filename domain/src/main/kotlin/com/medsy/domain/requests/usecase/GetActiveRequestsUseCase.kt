package com.medsy.domain.requests.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.repository.OrdersRepository
import com.medsy.domain.orders.usecase.DetermineOrderNextActionUseCase
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.model.MedicineRequestStatus
import com.medsy.domain.requests.model.ActiveRequestsLookup
import com.medsy.domain.requests.repository.RequestsRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetActiveRequestsUseCase @Inject constructor(
    private val requestsRepository: RequestsRepository,
    private val ordersRepository: OrdersRepository,
    private val determineOrderNextActionUseCase: DetermineOrderNextActionUseCase,
) {
    suspend operator fun invoke(
        nowMillis: Long = System.currentTimeMillis(),
    ): MedsyResult<ActiveRequestsLookup, MedsyError.Remote> = coroutineScope {
        val requestsDeferred = async { requestsRepository.getRequests(0, PAGE_SIZE, SORT) }
        val ordersDeferred = async { loadRecentOrders() }

        val requestPage = when (val result = requestsDeferred.await()) {
            is MedsyResult.Error -> return@coroutineScope result
            is MedsyResult.Success -> result.data
        }
        val potentiallyActive = requestPage.content.filter { it.status.isPotentiallyActive() }
        val recentOrders = when (val result = ordersDeferred.await()) {
            is MedsyResult.Error -> return@coroutineScope result
            is MedsyResult.Success -> result.data
        }

        val requestsWithMasterOrders = recentOrders.mapTo(mutableSetOf(), MasterOrder::requestId)
        val cutoff = nowMillis - SEARCH_DURATION_MILLIS
        val activeRequests = potentiallyActive
            .filter { it.createdAtMillis > cutoff && it.id !in requestsWithMasterOrders }
            .sortedByDescending(MedicineRequest::id)
        val resumableOrder = recentOrders
            .filter { determineOrderNextActionUseCase(it) != OrderNextAction.VIEW_DETAILS }
            .maxByOrNull(MasterOrder::id)

        MedsyResult.Success(ActiveRequestsLookup(activeRequests, resumableOrder))
    }

    private suspend fun loadRecentOrders(): MedsyResult<List<MasterOrder>, MedsyError.Remote> {
        val recentOrders = mutableListOf<MasterOrder>()
        var orderPage = 0
        while (true) {
            when (val result = ordersRepository.getOrders(orderPage, PAGE_SIZE, SORT)) {
                is MedsyResult.Error -> return result
                is MedsyResult.Success -> {
                    recentOrders += result.data.content
                    if (
                        result.data.last ||
                        orderPage + 1 >= MAX_ORDER_PAGES
                    ) break
                    orderPage++
                }
            }
        }
        return MedsyResult.Success(recentOrders)
    }

    private fun MedicineRequestStatus.isPotentiallyActive(): Boolean = this in setOf(
        MedicineRequestStatus.PENDING,
        MedicineRequestStatus.SEARCHING,
        MedicineRequestStatus.OFFERS_READY,
    )

    private companion object {
        const val PAGE_SIZE = 10
        const val SEARCH_DURATION_MILLIS = 15 * 60 * 1_000L
        const val MAX_ORDER_PAGES = 3
        val SORT = listOf("id,desc")
    }
}
