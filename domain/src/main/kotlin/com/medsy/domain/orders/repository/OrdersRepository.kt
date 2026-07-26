package com.medsy.domain.orders.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderPageDomain

interface OrdersRepository {
    suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageDomain, MedsyError.Remote>


    suspend fun getOrderById(id: Long): MedsyResult<OrderDetailsDomain, MedsyError.Remote>
}