package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(id: Long): Result<OrderDetailsDomain> {
        return ordersRepository.getOrderById(id)
    }
}