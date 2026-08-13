package com.medsy.data.requests.repository

import com.medsy.data.cart.mapper.toDomain
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.model.MedicineRequestPage
import com.medsy.domain.requests.repository.RequestsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : RequestsRepository {
    override suspend fun getRequestById(id: Long): MedsyResult<MedicineRequest, MedsyError.Remote> =
        safeApiCall { apiService.getRequestById(id) }.map { it.toDomain() }

    override suspend fun getRequests(
        page: Int,
        size: Int,
        sort: List<String>,
    ): MedsyResult<MedicineRequestPage, MedsyError.Remote> =
        safeApiCall { apiService.getCurrentCustomerRequests(page, size, sort) }
            .map { it.toDomain() }
}
