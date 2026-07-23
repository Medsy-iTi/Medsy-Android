package com.medsy.data.prescription.remote

import com.medsy.data.prescription.remote.dto.AnalyzedMedicineImageDto
import com.medsy.data.prescription.remote.dto.PrescriptionAnalysisDto
import com.medsy.data.productdetails.remote.ProductDetailsDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.search.remote.ProductsPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
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
        }
    }

    suspend fun analyzeMedicineImage(
        image: MultipartBody.Part,
    ): MedsyResult<List<AnalyzedMedicineImageDto>, MedsyError.Remote> {
        return safeApiCall {
            apiService.analyzeMedicineImage(image = image)
        }
    }

    suspend fun getProductById(
        id: Int,
        lang: String
    ): MedsyResult<ProductDetailsDto, MedsyError.Remote> {
        return safeApiCall {
            apiService.getProductById(id, lang)
        }
    }

    suspend fun searchProducts(
        query: String,
        page: Int = 0,
        size: Int = 50
    ): MedsyResult<ProductsPageDto, MedsyError.Remote> {
        return safeApiCall {
            if (query.isNotBlank()) {
                apiService.searchProducts(keyword = query, page = page, size = size, sort = null)
            } else {
                apiService.getProducts(page = page, size = size, sort = null)
            }
        }
    }
}
