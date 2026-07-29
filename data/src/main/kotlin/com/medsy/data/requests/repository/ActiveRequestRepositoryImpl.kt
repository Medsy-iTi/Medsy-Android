package com.medsy.data.requests.repository

import com.medsy.data.common.preferences.local.ActiveRequestsLocalDataSource
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.repository.ActiveRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveRequestRepositoryImpl @Inject constructor(
    private val localDataSource: ActiveRequestsLocalDataSource
) : ActiveRequestRepository {

    override fun observeActiveRequests(): Flow<List<MedicineRequest>> = localDataSource.activeRequests

    override suspend fun addActiveRequest(request: MedicineRequest) {
        val current = localDataSource.activeRequests.firstOrNull() ?: emptyList()
        val updated = current.toMutableList().apply { 
            removeAll { it.id == request.id }
            add(request)
        }
        localDataSource.setActiveRequests(updated)
    }

    override suspend fun removeActiveRequest(requestId: Long) {
        val current = localDataSource.activeRequests.firstOrNull() ?: emptyList()
        val updated = current.filter { it.id != requestId }
        localDataSource.setActiveRequests(updated)
    }

    override suspend fun clearActiveRequests() {
        localDataSource.setActiveRequests(emptyList())
    }
}
