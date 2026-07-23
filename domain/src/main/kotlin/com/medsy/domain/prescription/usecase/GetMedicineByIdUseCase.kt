package com.medsy.domain.prescription.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject

class GetMedicineByIdUseCase @Inject constructor(
    private val repository: PrescriptionRepository,
) {
    suspend operator fun invoke(
        productId: Int,
    ): MedsyResult<Medicine, MedsyError.Remote> =
        repository.getMedicineById(productId)
}
