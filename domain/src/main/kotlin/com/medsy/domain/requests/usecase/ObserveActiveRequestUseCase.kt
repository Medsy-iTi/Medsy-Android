package com.medsy.domain.requests.usecase

import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.repository.ActiveRequestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveActiveRequestUseCase @Inject constructor(
    private val repository: ActiveRequestRepository
) {
    operator fun invoke(): Flow<MedicineRequest?> = repository.observeActiveRequest()
}
