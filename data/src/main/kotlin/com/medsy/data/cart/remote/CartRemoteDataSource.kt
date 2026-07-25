package com.medsy.data.cart.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.common.EmptyMedsyResult
import okhttp3.MultipartBody
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import javax.inject.Inject

class CartRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getCart(): MedsyResult<CartDto, MedsyError.Remote> =
        safeApiCall(apiService::getCart)

    suspend fun addItem(
        productId: Int,
        quantity: Int,
    ): MedsyResult<CartDto, MedsyError.Remote> =
        safeApiCall {
            apiService.addCartItem(
                AddCartItemRequestDto(
                    productId = productId,
                    quantity = quantity,
                )
            )
        }

    suspend fun setItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): MedsyResult<CartDto, MedsyError.Remote> =
        safeApiCall {
            apiService.setCartItemQuantity(cartItemId, quantity)
        }

    suspend fun removeItem(
        cartItemId: Long,
    ): MedsyResult<CartDto, MedsyError.Remote> =
        safeApiCall {
            apiService.removeCartItem(cartItemId)
        }

    suspend fun clearCart(): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall(apiService::clearCart)

    suspend fun submitProductsRequest(
        request: ProductsRequestDto,
        multipartImage: MultipartBody.Part?
    ): MedsyResult<Long, MedsyError.Remote> =
        safeApiCall {
            apiService.submitProductsRequest(request, multipartImage)
        }.map { it.id }

}
