package com.medsy.data.requests.repository

import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.repository.ActiveRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveRequestRepositoryImpl @Inject constructor() : ActiveRequestRepository {
    private val _activeRequest = MutableStateFlow<MedicineRequest?>(null)

    override fun observeActiveRequest(): Flow<MedicineRequest?> = _activeRequest.asStateFlow()

    override suspend fun setActiveRequest(request: MedicineRequest) {
        _activeRequest.value = request
    }

    override suspend fun clearActiveRequest() {
        _activeRequest.value = null
    }
}
