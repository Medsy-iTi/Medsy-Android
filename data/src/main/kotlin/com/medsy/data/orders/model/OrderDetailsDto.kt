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
    @Json(name = "customerId") val customerId: Long,
    @Json(name = "customerName") val customerName: String?,
    @Json(name = "pharmacyId") val pharmacyId: Long?,
    @Json(name = "pharmacyName") val pharmacyName: String?,
    @Json(name = "pharmacyAddress") val pharmacyAddress: String?,
    @Json(name = "pharmacyPhone") val pharmacyPhone: String?,
    @Json(name = "pharmacistId") val pharmacistId: Long?,
    @Json(name = "pharmacistName") val pharmacistName: String?,
    @Json(name = "offerId") val offerId: Long?,
    @Json(name = "subTotal") val subTotal: Double,
    @Json(name = "deliveryFee") val deliveryFee: Double,
    @Json(name = "total") val total: Double,
    @Json(name = "deliveryLatitude") val deliveryLatitude: Double?,
    @Json(name = "deliveryLongitude") val deliveryLongitude: Double?,
    @Json(name = "status") val status: String?,
    @Json(name = "createdAt") val date: String,
    @Json(name = "items") val items: List<OrderItemDto>
)
