package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.prescription.model.PrescriptionImage
import javax.inject.Inject

class AttachCartPrescriptionUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local> = repository.attachPrescription(image)
}