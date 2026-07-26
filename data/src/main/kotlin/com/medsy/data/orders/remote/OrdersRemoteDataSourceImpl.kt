package com.medsy.data.orders.remote

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.model.OrderPageDataDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class OrdersRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : OrdersRemoteDataSource {

    override suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageDataDto, MedsyError.Remote> = safeApiCall {
        apiService.getCurrentCustomerOrders(page, size, sort)
    }

    override suspend fun getOrderById(id: Long): MedsyResult<OrderDetailsDto, MedsyError.Remote> = safeApiCall {
        apiService.getOrderById(id)
    }
}
