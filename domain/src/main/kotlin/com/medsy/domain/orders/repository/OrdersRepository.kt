package com.medsy.domain.orders.repository

import com.medsy.domain.orders.model.Order

interface OrdersRepository {
    suspend fun getOrders(): List<Order>
}