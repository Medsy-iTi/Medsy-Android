package com.medsy.domain.prescription.usecase

import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class PreparePrescriptionCaptureUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(): Result<PrescriptionImage> =
        repository.prepareCameraImage()
}
