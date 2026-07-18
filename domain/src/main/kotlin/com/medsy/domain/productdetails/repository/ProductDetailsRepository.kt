package com.medsy.domain.productdetails.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.productdetails.model.ProductDetails

interface ProductDetailsRepository {
    suspend fun getProductById(id: Int, language: String): MedsyResult<ProductDetails, MedsyError.Remote>
}
