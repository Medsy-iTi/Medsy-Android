package com.medsy.data.prescription.remote

import com.medsy.data.prescription.remote.dto.PrescriptionAnalysisDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import okhttp3.MultipartBody
import javax.inject.Inject

class PrescriptionRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun analyzePrescription(
        image: MultipartBody.Part,
    ): MedsyResult<PrescriptionAnalysisDto, MedsyError.Remote> {

        return safeApiCall {
            apiService.analyzePrescription(image = image)
        }.onSuccess {
        }.onError { error ->
            error.toString()
        }
    }
}
