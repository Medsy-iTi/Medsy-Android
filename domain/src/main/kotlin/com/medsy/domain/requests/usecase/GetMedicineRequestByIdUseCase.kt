package com.medsy.domain.requests.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.requests.model.MedicineRequestDetails
import com.medsy.domain.requests.repository.RequestsRepository
import javax.inject.Inject

class GetMedicineRequestByIdUseCase @Inject constructor(
    private val repository: RequestsRepository,
) {
    suspend operator fun invoke(id: Long): MedsyResult<MedicineRequestDetails, MedsyError.Remote> =
        repository.getRequestById(id)
}
