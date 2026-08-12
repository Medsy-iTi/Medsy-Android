package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.MasterOrderPage
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 10,
        sort: List<String>? = null
    ): MedsyResult<MasterOrderPage, MedsyError.Remote> {
        return ordersRepository.getOrders(page = page, size = size, sort = sort)
    }
}
