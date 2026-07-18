package com.medsy.data.productdetails.repository

import com.medsy.data.productdetails.mapper.toDomain
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.productdetails.model.ProductDetails
import com.medsy.domain.productdetails.repository.ProductDetailsRepository
import javax.inject.Inject

class ProductDetailsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductDetailsRepository {

    override suspend fun getProductById(
        id: Int,
        language: String
    ): MedsyResult<ProductDetails, MedsyError.Remote> {

        return safeApiCall {
            apiService.getProductById(id, language)
        }.map { productDto ->
            productDto.toDomain()
        }
    }
}