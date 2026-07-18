package com.medsy.domain.prescription.usecase

import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class AddPrescriptionToCartUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(request: PrescriptionCartRequest): Result<Unit> =
        repository.addPrescriptionToCart(request)
}
