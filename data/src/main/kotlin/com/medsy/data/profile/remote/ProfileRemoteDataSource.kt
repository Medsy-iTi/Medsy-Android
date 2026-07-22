package com.medsy.data.profile.remote

import com.medsy.data.profile.remote.dto.CustomerDto
import com.medsy.data.profile.remote.dto.UpdateCustomerProfileRequestDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun getCurrentProfile(): MedsyResult<CustomerDto, MedsyError.Remote> = safeApiCall {
        apiService.getCurrentCustomer()
    }

    suspend fun updateCurrentProfile(
        request: UpdateCustomerProfileRequestDto,
    ): MedsyResult<CustomerDto, MedsyError.Remote> = safeApiCall {
        apiService.updateCurrentCustomer(
            request = request,
        )
    }
}
