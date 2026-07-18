package com.medsy.domain.prescription.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class AddPrescriptionToCartUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(
        request: PrescriptionCartRequest,
    ): EmptyMedsyResult<MedsyError.Local> =
        repository.addPrescriptionToCart(request)
}
