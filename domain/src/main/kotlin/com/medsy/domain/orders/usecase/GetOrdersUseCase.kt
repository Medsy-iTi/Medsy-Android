package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 10,
        sort: List<String>? = null
    ): Result<OrderPageDomain> {
        return ordersRepository.getOrders(page = page, size = size, sort = sort)
    }
}