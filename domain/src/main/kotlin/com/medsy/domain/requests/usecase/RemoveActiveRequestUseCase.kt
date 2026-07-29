package com.medsy.domain.requests.usecase

import com.medsy.domain.requests.repository.ActiveRequestRepository
import javax.inject.Inject

class RemoveActiveRequestUseCase @Inject constructor(
    private val repository: ActiveRequestRepository
) {
    suspend operator fun invoke(requestId: Long) {
        repository.removeActiveRequest(requestId)
    }
}
