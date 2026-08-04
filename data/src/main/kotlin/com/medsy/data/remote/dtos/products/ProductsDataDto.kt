package com.medsy.data.remote.dtos.products

import com.medsy.data.search.dto.ProductDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductsDataDto(
    val content: List<ProductDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)
