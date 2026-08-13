package com.medsy.data.orders.remote

import com.medsy.data.orders.model.MasterOrderDto
import com.medsy.data.orders.model.MasterOrderPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface OrdersRemoteDataSource {
    suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<MasterOrderPageDto, MedsyError.Remote>

    suspend fun getOrderById(id: Long): MedsyResult<MasterOrderDto, MedsyError.Remote>
}
