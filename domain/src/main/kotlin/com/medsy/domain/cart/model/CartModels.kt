package com.medsy.domain.cart.model

import com.medsy.domain.prescription.model.PrescriptionImage

data class Cart(
    val id: Long,
    val items: List<CartItem>,
    val totalPriceEgp: Double,
)

data class CartItem(
    val id: Long,
    val productId: Int,
    val productName: String,
    val imageUrl: String,
    val unitPriceEgp: Double,
    val quantity: Int,
    val subtotalEgp: Double,
)

data class CartItemInput(
    val productId: Int,
    val quantity: Int,
)

data class CartDraft(
    val prescriptionImage: PrescriptionImage? = null,
    val pharmacistNote: String = "",
)

sealed interface AddCartItemsOutcome {
    data class Complete(val cart: Cart) : AddCartItemsOutcome
    data class Partial(
        val cart: Cart,
        val addedItemsCount: Int,
    ) : AddCartItemsOutcome
}
