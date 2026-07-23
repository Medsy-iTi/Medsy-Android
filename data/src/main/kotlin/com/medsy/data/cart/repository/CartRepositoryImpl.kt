package com.medsy.data.cart.repository

import com.medsy.data.cart.local.CartDraftStorage
import com.medsy.data.cart.mapper.toDomain
import com.medsy.data.cart.remote.CartRemoteDataSource
import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.model.CartDraft
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.prescription.model.PrescriptionImage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource,
    private val draftStorage: CartDraftStorage,
) : CartRepository {
    override val draft: Flow<CartDraft> = draftStorage.draft

    override suspend fun getCart(): MedsyResult<Cart, MedsyError.Remote> =
        remoteDataSource.getCart().map { it.toDomain() }

    override suspend fun addItem(
        productId: Int,
        quantity: Int,
    ): MedsyResult<Cart, MedsyError.Remote> =
        remoteDataSource.addItem(productId, quantity).map { it.toDomain() }

    override suspend fun setItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): MedsyResult<Cart, MedsyError.Remote> =
        remoteDataSource.setItemQuantity(cartItemId, quantity).map { it.toDomain() }

    override suspend fun removeItem(
        cartItemId: Long,
    ): MedsyResult<Cart, MedsyError.Remote> =
        remoteDataSource.removeItem(cartItemId).map { it.toDomain() }

    override suspend fun clearCart(): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.clearCart()

    override suspend fun submitProductsRequest(
        request: ProductsRequest,
    ): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.submitProductsRequest()

    override suspend fun updateNote(
        note: String,
    ): EmptyMedsyResult<MedsyError.Local> = draftStorage.updateNote(note)

    override suspend fun attachPrescription(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local> = draftStorage.attachPrescription(image)

    override suspend fun removePrescription(): EmptyMedsyResult<MedsyError.Local> =
        draftStorage.removePrescription()

    override suspend fun clearDraft(): EmptyMedsyResult<MedsyError.Local> =
        draftStorage.clear()
}
