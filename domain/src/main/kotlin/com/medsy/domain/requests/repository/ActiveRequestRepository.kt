package com.medsy.domain.requests.repository

import com.medsy.domain.requests.model.MedicineRequest
import kotlinx.coroutines.flow.Flow

interface ActiveRequestRepository {
    fun observeActiveRequest(): Flow<MedicineRequest?>
    suspend fun setActiveRequest(request: MedicineRequest)
    suspend fun clearActiveRequest()
}
