package com.medsy.domain.requests.usecase

import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.repository.ActiveRequestRepository
import javax.inject.Inject

class AddActiveRequestUseCase @Inject constructor(
    private val repository: ActiveRequestRepository
) {
    suspend operator fun invoke(request: MedicineRequest) {
        repository.addActiveRequest(request)
    }
}
