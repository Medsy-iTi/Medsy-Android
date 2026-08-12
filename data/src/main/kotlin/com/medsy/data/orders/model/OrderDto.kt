
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
    @Json(name = "content") val content: List<OrderGroupDto>,
    @Json(name = "pageNumber") val pageNumber: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalElements") val totalElements: Int,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "last") val last: Boolean
)

@JsonClass(generateAdapter = true)
data class OrderGroupDto(
    @Json(name = "requestId") val requestId: Long,
    @Json(name = "orders") val orders: List<OrderDto>,
)

@JsonClass(generateAdapter = true)
data class OrderDto(
    @Json(name = "id") val id: Long,
    @Json(name = "customerId") val customerId: Long,
    @Json(name = "customerName") val customerName: String? = null,
    @Json(name = "customerNotes") val customerNotes: String? = null,
    @Json(name = "deliveryAddress") val deliveryAddress: String? = null,
    @Json(name = "phoneNumber") val phoneNumber: String? = null,
    @Json(name = "prescriptionUrl") val prescriptionUrl: String? = null,
    @Json(name = "pharmacyId") val pharmacyId: Long? = null,
    @Json(name = "pharmacyName") val pharmacyName: String? = null,
    @Json(name = "pharmacyAddress") val pharmacyAddress: String? = null,
    @Json(name = "pharmacyPhone") val pharmacyPhone: String? = null,
    @Json(name = "pharmacistId") val pharmacistId: Long? = null,
    @Json(name = "pharmacistName") val pharmacistName: String? = null,
    @Json(name = "offerId") val offerId: Long? = null,
    @Json(name = "subTotal") val subTotal: Double,
    @Json(name = "deliveryFee") val deliveryFee: Double? = null,
    @Json(name = "total") val total: Double,
    @Json(name = "deliveryLatitude") val deliveryLatitude: Double? = null,
    @Json(name = "deliveryLongitude") val deliveryLongitude: Double? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "paymentMethod") val paymentMethod: String? = null,
    @Json(name = "paymentStatus") val paymentStatus: String? = null,
    @Json(name = "paidAt") val paidAt: String? = null,
    @Json(name = "items") val items: List<OrderItemDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class OrderItemDto(
    @Json(name = "id") val id: Long,
    @Json(name = "productId") val productId: Long,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "unitPrice") val unitPrice: Double,
    @Json(name = "product") val product: OrderProductDto,
    @Json(name = "totalPrice") val totalPrice: Double? = null,
)

@JsonClass(generateAdapter = true)
data class OrderProductDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "productName") val productName: String?,
    @Json(name = "strength") val strength: String?,
    @Json(name = "packSize") val packSize: String?,
    @Json(name = "form") val form: String?,
    @Json(name = "price") val price: Double,
    @Json(name = "scientificName") val scientificName: String,
    @Json(name = "company") val company: String?,
    @Json(name = "route") val route: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "imageUrl") val imageUrl: String?,
)
