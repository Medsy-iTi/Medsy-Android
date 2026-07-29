
package com.medsy.data.orders.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String?,
    @Json(name = "data") val data: OrderPageDataDto?
)

@JsonClass(generateAdapter = true)
data class OrderPageDataDto(
    @Json(name = "content") val content: List<OrderDto>,
    @Json(name = "pageNumber") val pageNumber: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalElements") val totalElements: Int,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "last") val last: Boolean
)

@JsonClass(generateAdapter = true)
data class OrderDto(
    @Json(name = "id") val id: Long,
    @Json(name = "customerId") val customerId: Long,
    @Json(name = "customerName") val customerName: String? = null,
    @Json(name = "pharmacyId") val pharmacyId: Long? = null,
    @Json(name = "pharmacyName") val pharmacyName: String? = null,
    @Json(name = "pharmacyAddress") val pharmacyAddress: String? = null,
    @Json(name = "pharmacyPhone") val pharmacyPhone: String? = null,
    @Json(name = "pharmacistId") val pharmacistId: Long? = null,
    @Json(name = "pharmacistName") val pharmacistName: String? = null,
    @Json(name = "offerId") val offerId: Long? = null,
    @Json(name = "subTotal") val subTotal: Double,
    @Json(name = "deliveryFee") val deliveryFee: Double,
    @Json(name = "total") val total: Double,
    @Json(name = "deliveryLatitude") val deliveryLatitude: Double? = null,
    @Json(name = "deliveryLongitude") val deliveryLongitude: Double? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "createdAt") val date: String? = null,
    @Json(name = "prescriptionImage") val prescriptionImage: String? = null,
    @Json(name = "customerNote") val customerNote: String? = null,
    @Json(name = "pharmacyNote") val pharmacyNote: String? = null,
    @Json(name = "items") val items: List<OrderItemDto>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class OrderItemDto(
    @Json(name = "id") val id: Long,
    @Json(name = "productId") val productId: Long,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "unitPrice") val unitPrice: Double,
    @Json(name = "totalPrice") val totalPrice: Double? = null,
    @Json(name = "productName") val productName: String? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null
)