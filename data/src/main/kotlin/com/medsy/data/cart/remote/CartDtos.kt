package com.medsy.data.cart.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddCartItemRequestDto(
    val productId: Int,
    val quantity: Int,
)

@JsonClass(generateAdapter = true)
data class BulkCartItemsRequestDto(
    val items: List<CartItemInputDto>
)

@JsonClass(generateAdapter = true)
data class CartItemInputDto(
    val productId: Int,
    val quantity: Int,
)

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
data class CartItemDto(
    val id: Long,
    val productId: Int,
    val productName: String? = null,
    val imageUrl: String? = null,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double,
    val product: CartItemProductDto? = null,
)

@JsonClass(generateAdapter = true)
data class CartItemProductDto(
    val id: Long? = null,
    val name: String? = null,
    val productName: String? = null,
    val imageUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class CartDto(
    val id: Long,
    val items: List<CartItemDto>,
    val totalPrice: Double,
)
