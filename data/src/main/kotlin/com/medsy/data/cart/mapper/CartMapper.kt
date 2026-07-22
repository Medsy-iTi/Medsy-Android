package com.medsy.data.cart.mapper

import com.medsy.data.cart.remote.CartDto
import com.medsy.data.cart.remote.CartItemDto
import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.model.CartItem

fun CartDto.toDomain(): Cart = Cart(
    id = id,
    items = items.map(CartItemDto::toDomain),
    totalPriceEgp = totalPrice,
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
