package com.medsy.domain.orders.repository

import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderPageDomain

interface OrdersRepository {
    suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<OrderPageDomain>


    suspend fun getOrderById(id: Long): Result<OrderDetailsDomain>
}