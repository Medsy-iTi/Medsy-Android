package com.medsy.data.cart.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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

    suspend fun submitProductsRequest(): EmptyMedsyResult<MedsyError.Remote> {
        delay(300L.milliseconds)
        return MedsyResult.Success(Unit)
    }

}
