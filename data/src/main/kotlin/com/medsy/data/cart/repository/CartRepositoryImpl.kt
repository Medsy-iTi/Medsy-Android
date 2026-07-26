package com.medsy.data.cart.repository

import com.medsy.data.cart.local.CartDraftStorage
import com.medsy.data.cart.mapper.toDomain
import com.medsy.data.cart.remote.CartItemInputDto
import com.medsy.data.cart.remote.CartRemoteDataSource
import com.medsy.data.cart.remote.ProductsRequestDto
import com.medsy.data.common.media.PrescriptionImageStorage
import com.medsy.data.prescription.remote.PrescriptionImageMimeType
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource,
    private val draftStorage: CartDraftStorage,
    private val imageStorage: PrescriptionImageStorage,
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
    ): MedsyResult<Long, MedsyError.Remote> {
        var multipartImage: MultipartBody.Part? = null
        if (request.prescriptionImage != null) {
            val file = imageStorage.getFile(request.prescriptionImage!!)
            if (file.exists()) {
                val mimeType = PrescriptionImageMimeType.fromExtension(file.extension).value
                val requestFile = file.asRequestBody(mimeType.toMediaType())
                multipartImage = MultipartBody.Part.createFormData("prescription", file.name, requestFile)
            }
        }
        
        val requestDto = ProductsRequestDto(
            items = request.items.map { CartItemInputDto(it.productId, it.quantity) },
            notes = request.notes,
            deliveryMethod = request.deliveryMethod.name,
            deliveryAddress = request.deliveryAddress,
            deliveryLatitude = request.deliveryLatitude,
            deliveryLongitude = request.deliveryLongitude,
            paymentMethod = request.paymentMethod.name
        )
        return remoteDataSource.submitProductsRequest(requestDto, multipartImage)
    }

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
