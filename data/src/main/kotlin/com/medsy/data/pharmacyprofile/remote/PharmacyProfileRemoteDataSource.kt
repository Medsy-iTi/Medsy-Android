package com.medsy.data.pharmacyprofile.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class PharmacyProfileRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getPharmacyById(
        pharmacyId: Long,
    ): MedsyResult<PharmacyProfileDto, MedsyError.Remote> = safeApiCall {
        apiService.getPharmacyById(pharmacyId)
    }
}
