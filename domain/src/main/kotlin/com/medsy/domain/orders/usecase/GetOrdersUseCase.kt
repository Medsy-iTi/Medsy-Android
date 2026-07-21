package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.Order
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(): List<Order> {
        return ordersRepository.getOrders()
    }
}