package com.medsy.domain.prescription.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class ImportPrescriptionImageUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(
        sourceUri: String,
    ): MedsyResult<PrescriptionImage, MedsyError.Local> =
        repository.importGalleryImage(sourceUri)
}
