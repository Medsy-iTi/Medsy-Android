package com.medsy.data.cart.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddCartItemRequestDto(
    val productId: Int,
    val quantity: Int,
)

@JsonClass(generateAdapter = true)
data class CartItemDto(
    val id: Long,
    val productId: Int,
    val productName: String,
    val imageUrl: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double,
)

@JsonClass(generateAdapter = true)
data class CartDto(
    val id: Long,
    val items: List<CartItemDto>,
    val totalPrice: Double,
)
