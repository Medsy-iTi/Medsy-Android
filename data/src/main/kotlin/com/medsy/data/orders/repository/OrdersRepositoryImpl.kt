package com.medsy.data.orders.repository

import com.medsy.data.orders.mapper.toDomain
import com.medsy.data.remote.api.ApiService
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrdersRepository {

    override suspend fun getOrders(
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<OrderPageDomain> {
        return try {
            val response = apiService.getCurrentCustomerOrders(
                page = page,
                size = size,
                sort = sort
            )

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown error occurred"))
                }
            } else {
                Result.failure(Exception("Failed to fetch orders: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getOrderById(id: Long): Result<OrderDetailsDomain> {
        return try {
            val response = apiService.getOrderById(id)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data.toDomain())
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown error occurred"))
                }
            } else {
                Result.failure(Exception("Failed to fetch order details: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}