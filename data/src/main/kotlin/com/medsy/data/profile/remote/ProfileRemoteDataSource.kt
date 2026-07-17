package com.medsy.data.profile.remote

import com.medsy.data.profile.remote.dto.CustomerDto
import com.medsy.data.profile.remote.dto.UpdateCustomerProfileRequestDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.ApiResult
import com.medsy.data.remote.network.safeApiCall
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun getCurrentProfile(): ApiResult<CustomerDto> = safeApiCall {
        apiService.getCurrentCustomer()
    }

    suspend fun updateCurrentProfile(
        homeAddress: String?,
        dob: String?,
    ): ApiResult<CustomerDto> = safeApiCall {
        apiService.updateCurrentCustomer(
            request = UpdateCustomerProfileRequestDto(
                homeAddress = homeAddress,
                dob = dob,
            )
        )
    }
}
