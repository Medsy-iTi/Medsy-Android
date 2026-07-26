package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.ProductsRequest
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class SubmitProductsRequestUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        request: ProductsRequest,
    ): EmptyMedsyResult<MedsyError> {
        val hasRequestContent = request.items.isNotEmpty() || request.prescriptionImage != null
        val hasValidDeliveryAddress = request.deliveryMethod != DeliveryMethod.DELIVERY || (
                request.deliveryAddress?.isNotBlank() == true &&
                        request.deliveryLatitude?.let { it in -90.0..90.0 } == true &&
                        request.deliveryLongitude?.let { it in -180.0..180.0 } == true
                )

        if (!hasRequestContent || !hasValidDeliveryAddress) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }

        val normalizedRequest = request.copy(
            note = request.note?.trim()?.takeIf(String::isNotBlank),
            deliveryAddress = request.deliveryAddress?.trim()?.takeIf(String::isNotBlank),
            deliveryLatitude = request.deliveryLatitude.takeIf {
                request.deliveryMethod == DeliveryMethod.DELIVERY
            },
            deliveryLongitude = request.deliveryLongitude.takeIf {
                request.deliveryMethod == DeliveryMethod.DELIVERY
            },
        )

        return cartRepository.submitProductsRequest(normalizedRequest)
    }
}
