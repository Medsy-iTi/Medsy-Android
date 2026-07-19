package com.medsy.domain.productdetails.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.productdetails.model.ProductDetails
import com.medsy.domain.productdetails.repository.ProductDetailsRepository
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductDetailsRepository
) {
    suspend operator fun invoke(
        id: Int,
        language: String
    ): MedsyResult<ProductDetails, MedsyError.Remote> {
        return repository.getProductById(id, language)
    }
}