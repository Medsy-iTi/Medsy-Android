package com.medsy.data.cart.mapper

import com.medsy.data.cart.remote.CartDto
import com.medsy.data.cart.remote.CartItemDto
import com.medsy.data.cart.remote.CartItemInputDto
import com.medsy.data.cart.remote.ProductsRequestDto
import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.model.CartItem
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.cart.model.PaymentOption

fun CartItemInput.toDto(): CartItemInputDto = CartItemInputDto(
    productId = productId,
    quantity = quantity,
)

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
    paymentMethod = when (paymentMethod) {
        PaymentOption.CASH -> "CASH"
        PaymentOption.VISA -> "CARD"
    },
)

private fun CartItemDto.toDomain(): CartItem = CartItem(
    id = id,
    productId = productId,
    productName = product.name.ifBlank { product.productName.orEmpty() },
    imageUrl = product.imageUrl?.takeIf(String::isNotBlank),
    unitPriceEgp = unitPrice,
    quantity = quantity,
    subtotalEgp = subtotal,
)
