package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.asEmptyDataResult
import com.medsy.domain.common.onSuccess
import javax.inject.Inject

class SubmitProductsRequestUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        request: ProductsRequest,
    ): EmptyMedsyResult<MedsyError> {
        if (!request.isValidRequest()) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }

        return cartRepository.submitProductsRequest(request.normalizeRequest())
            .onSuccess {
                cartRepository.clearDraft()
            }
            .asEmptyDataResult()
    }

    private fun ProductsRequest.isValidRequest(): Boolean {
        val hasProducts = items.isNotEmpty()

        val hasValidDeliveryAddress = deliveryMethod !=
                DeliveryMethod.DELIVERY || (
                deliveryAddress?.isNotBlank() == true &&
                        deliveryLatitude?.let { it in -90.0..90.0 } == true &&
                        deliveryLongitude?.let { it in -180.0..180.0 } == true
                )

        return (hasProducts && hasValidDeliveryAddress)
    }

    private fun ProductsRequest.normalizeRequest(): ProductsRequest =
        this.copy(
            notes = notes?.trim()?.takeIf(String::isNotBlank),
            deliveryAddress = deliveryAddress?.trim()?.takeIf(String::isNotBlank),
            deliveryLatitude = deliveryLatitude.takeIf {
                deliveryMethod == DeliveryMethod.DELIVERY
            },
            deliveryLongitude = deliveryLongitude.takeIf {
                deliveryMethod == DeliveryMethod.DELIVERY
            },
        )
}
