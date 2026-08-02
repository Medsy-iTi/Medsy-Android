package com.medsy.data.requests.repository

import com.medsy.data.cart.mapper.toDomain
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.requests.model.MedicineRequestDetails
import com.medsy.domain.requests.repository.RequestsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : RequestsRepository {
    override suspend fun getRequestById(id: Long): MedsyResult<MedicineRequestDetails, MedsyError.Remote> =
        safeApiCall { apiService.getRequestById(id) }.map { it.toDomain() }
}
