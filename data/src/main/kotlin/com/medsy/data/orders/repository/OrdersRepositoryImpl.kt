package com.medsy.data.orders.repository

import com.medsy.data.orders.mapper.toDomain
import com.medsy.data.orders.remote.OrdersRemoteDataSource
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrdersRemoteDataSource
) : OrdersRepository {

    override suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageDomain, MedsyError.Remote> {
        return remoteDataSource.getOrders(page, size, sort).map { it.toDomain() }
    }

    override suspend fun getOrderById(id: Long): MedsyResult<OrderDetailsDomain, MedsyError.Remote> {
        return remoteDataSource.getOrderById(id).map { it.toDomain() }
    }
}