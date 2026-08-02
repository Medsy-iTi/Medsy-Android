package com.medsy.domain.requests.repository

import com.medsy.domain.requests.model.MedicineRequest
import kotlinx.coroutines.flow.Flow

interface ActiveRequestRepository {
    fun observeActiveRequests(): Flow<List<MedicineRequest>>
    suspend fun addActiveRequest(request: MedicineRequest)
    suspend fun removeActiveRequest(requestId: Long)
    suspend fun clearActiveRequests()
}
