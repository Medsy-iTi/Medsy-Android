package com.medsy.domain.cart.repository

import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.model.CartDraft
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.model.InteractionWarning
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.PrescriptionImage
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    val draft: Flow<CartDraft>
    val cartItemCount: Flow<Int>

    suspend fun getCart(): MedsyResult<Cart, MedsyError.Remote>
    suspend fun addItem(
        productId: Int,
        quantity: Int,
    ): MedsyResult<Cart, MedsyError.Remote>

    suspend fun addItemsBulk(items: List<CartItemInput>): EmptyMedsyResult<MedsyError.Remote>

    suspend fun setItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): MedsyResult<Cart, MedsyError.Remote>

    suspend fun removeItem(cartItemId: Long): MedsyResult<Cart, MedsyError.Remote>
    suspend fun clearCart(): EmptyMedsyResult<MedsyError.Remote>

    /** AI drug-interaction warnings for the current cart contents. */
    suspend fun getCartInteractions(): MedsyResult<List<InteractionWarning>, MedsyError.Remote>

    suspend fun submitProductsRequest(
        request: ProductsRequest,
    ): MedsyResult<Long, MedsyError.Remote>

    suspend fun updateNote(note: String): EmptyMedsyResult<MedsyError.Local>
    suspend fun attachPrescription(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local>

    suspend fun removePrescription(): EmptyMedsyResult<MedsyError.Local>
    suspend fun clearDraft(): EmptyMedsyResult<MedsyError.Local>
}
