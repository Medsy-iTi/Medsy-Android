package com.medsy.domain.prescription.usecase

import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class ExtractPrescriptionUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(
        image: PrescriptionImage,
    ): Result<PrescriptionExtractionOutcome> = repository.extractPrescription(image)
}
