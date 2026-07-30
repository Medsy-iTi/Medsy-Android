package com.medsy.data.orders.remote

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderPageDataDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface OrdersRemoteDataSource {
    suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageDataDto, MedsyError.Remote>

    suspend fun getOrderById(id: Long): MedsyResult<OrderDetailsDto, MedsyError.Remote>
}
