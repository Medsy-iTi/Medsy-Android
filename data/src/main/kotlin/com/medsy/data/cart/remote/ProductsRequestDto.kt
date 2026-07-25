package com.medsy.data.cart.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductsRequestDto(
    val items: List<CartItemInputDto>,
    val notes: String?,
    val deliveryMethod: String,
    val deliveryAddress: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val paymentMethod: String,
)

@JsonClass(generateAdapter = true)
data class CartItemInputDto(
    val productId: Int,
    val quantity: Int,
)
