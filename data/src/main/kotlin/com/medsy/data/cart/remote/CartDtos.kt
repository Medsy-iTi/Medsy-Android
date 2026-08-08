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
    val unitPrice: Double,
    val quantity: Int,
    val product: CartItemProductDto,
    val subtotal: Double,
)

@JsonClass(generateAdapter = true)
data class CartItemProductDto(
    val id: Long,
    val name: String,
    val productName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val price: Double,
    val scientificName: String,
    val company: String?,
    val route: String?,
    val description: String?,
    val imageUrl: String?,
)

@JsonClass(generateAdapter = true)
data class CartDto(
    val id: Long,
    val items: List<CartItemDto>,
    val totalPrice: Double,
)
