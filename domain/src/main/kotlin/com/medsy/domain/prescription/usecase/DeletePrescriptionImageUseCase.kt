package com.medsy.domain.prescription.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class DeletePrescriptionImageUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local> =
        repository.deleteImage(image)
}
