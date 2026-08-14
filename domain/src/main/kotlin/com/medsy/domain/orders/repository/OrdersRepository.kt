package com.medsy.domain.orders.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.MasterOrderPage

interface OrdersRepository {
    suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<MasterOrderPage, MedsyError.Remote>


    suspend fun getOrderById(id: Long): MedsyResult<MasterOrder, MedsyError.Remote>
}
