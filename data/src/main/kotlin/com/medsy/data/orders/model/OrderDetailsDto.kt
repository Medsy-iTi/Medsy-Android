package com.medsy.data.orders.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderDetailsResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String?,
    @Json(name = "data") val data: OrderDetailsDto?
)

@JsonClass(generateAdapter = true)
data class OrderDetailsDto(
    @Json(name = "id") val id: Long,
    @Json(name = "userId") val userId: Long,
    @Json(name = "pharmacyId") val pharmacyId: Long?,
    @Json(name = "pharmacistId") val pharmacistId: Long?,
    @Json(name = "offerId") val offerId: Long?,
    @Json(name = "totalPrice") val totalPrice: Double,
    @Json(name = "deliveryLatitude") val deliveryLatitude: Double?,
    @Json(name = "deliveryLongitude") val deliveryLongitude: Double?,
    @Json(name = "status") val status: String?,
    @Json(name = "createdAt") val date: String,
    @Json(name = "items") val items: List<OrderItemDto>
)
