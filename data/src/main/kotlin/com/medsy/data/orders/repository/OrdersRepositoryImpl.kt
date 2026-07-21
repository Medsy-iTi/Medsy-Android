package com.medsy.data.orders.repository


import com.medsy.data.remote.api.ApiService
import com.medsy.domain.orders.model.Order
import com.medsy.domain.orders.model.OrderProductThumbnailDomain
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.repository.OrdersRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrdersRepository {

    override suspend fun getOrders(): List<Order> {
        delay(300)

        return listOf(
            Order(
                id = "1258",
                dateLabel = "Today",
                status = OrderStatus.Confirmed,
                pharmacyName = "Al Rahma Pharmacy",
                total = 180,
                productCount = 3,
                productThumbnails = listOf(
                    OrderProductThumbnailDomain("1", "https://example.com/images/product_1.png"),
                    OrderProductThumbnailDomain("2", "https://example.com/images/panadol.png"),
                    OrderProductThumbnailDomain("3", "https://example.com/images/panadol.png"),
                ),
            ),
            Order(
                id = "1230",
                dateLabel = "Yesterday",
                status = OrderStatus.Delivered,
                pharmacyName = "Al Shifa Pharmacy",
                total = 125,
                productCount = 2,
                productThumbnails = listOf(
                    OrderProductThumbnailDomain("4", "https://example.com/images/panadol.png"),
                    OrderProductThumbnailDomain("5", "https://example.com/images/panadol.png"),
                ),
            ),
            Order(
                id = "1205",
                dateLabel = "May 12",
                status = OrderStatus.Delivered,
                pharmacyName = "El Ezaby Pharmacy",
                total = 240,
                productCount = 4,
                productThumbnails = listOf(
                    OrderProductThumbnailDomain("6", "https://example.com/images/product_2.png"),
                    OrderProductThumbnailDomain("7", "https://example.com/images/panadol.png"),
                    OrderProductThumbnailDomain("8", "https://example.com/images/panadol.png"),
                ),
            ),
            Order(
                id = "1180",
                dateLabel = "May 9",
                status = OrderStatus.Cancelled,
                pharmacyName = null,
                total = 0,
                productCount = 2,
                productThumbnails = listOf(
                    OrderProductThumbnailDomain("9", "https://example.com/images/product_3.png"),
                    OrderProductThumbnailDomain("10", "https://example.com/images/product_4.png"),
                ),
            ),
        )
    }
}