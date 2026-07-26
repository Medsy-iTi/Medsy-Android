package com.medsy.data.cart.mapper

import com.medsy.data.cart.remote.CartDto
import com.medsy.data.cart.remote.CartItemInputDto
import com.medsy.data.cart.remote.CartItemDto
import com.medsy.data.cart.remote.ProductsRequestDto
import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.model.CartItem
import com.medsy.domain.cart.model.ProductsRequest

fun CartDto.toDomain(): Cart = Cart(
    id = id,
    items = items.map(CartItemDto::toDomain),
    totalPriceEgp = totalPrice,
)

fun ProductsRequest.toDto(): ProductsRequestDto = ProductsRequestDto(
    items = items.map { item ->
        CartItemInputDto(
            productId = item.productId,
            quantity = item.quantity,
        )
    },
    notes = notes,
    deliveryMethod = deliveryMethod.name,
    deliveryAddress = deliveryAddress,
    deliveryLatitude = deliveryLatitude,
    deliveryLongitude = deliveryLongitude,
    paymentMethod = paymentMethod.name,
)

private fun CartItemDto.toDomain(): CartItem = CartItem(
    id = id,
    productId = productId,
    productName = productName,
    imageUrl = imageUrl,
    unitPriceEgp = unitPrice,
    quantity = quantity,
    subtotalEgp = subtotal,
)
