package com.medsy.domain.orders.repository

import com.medsy.domain.orders.model.OrderPageDomain

interface OrdersRepository {
    suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<OrderPageDomain>
}