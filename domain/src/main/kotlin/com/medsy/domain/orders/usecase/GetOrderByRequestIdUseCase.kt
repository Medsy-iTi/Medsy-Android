package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject
import kotlinx.coroutines.delay

class GetOrderByRequestIdUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(requestId: Long): MedsyResult<MasterOrder, MedsyError.Remote> {
        for (attempt in 0 until MAX_ATTEMPTS) {
            for (page in 0 until MAX_PAGES) {
                when (val result = repository.getOrders(page, PAGE_SIZE, SORT)) {
                    is MedsyResult.Error -> return result
                    is MedsyResult.Success -> {
                        result.data.content.firstOrNull { it.requestId == requestId }?.let {
                            return MedsyResult.Success(it)
                        }
                        if (result.data.last) break
                    }
                }
            }
            if (attempt < MAX_ATTEMPTS - 1) delay(RETRY_DELAY_MILLIS)
        }
        return MedsyResult.Error(MedsyError.Remote.EmptyResponse)
    }

    private companion object {
        const val PAGE_SIZE = 10
        const val MAX_PAGES = 3
        const val MAX_ATTEMPTS = 3
        const val RETRY_DELAY_MILLIS = 500L
        val SORT = listOf("id,desc")
    }
}
