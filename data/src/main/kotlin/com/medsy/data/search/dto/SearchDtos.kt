package com.medsy.data.search.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "productName") val productName: String?,
    @Json(name = "strength") val strength: String?,
    @Json(name = "packSize") val packSize: String?,
    @Json(name = "form") val form: String?,
    @Json(name = "scientificName") val scientificName: String,
    @Json(name = "scientificCategory") val scientificCategory: String?,
    @Json(name = "price") val price: Double,
    @Json(name = "imageUrl") val imageUrl: String?,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "consumerCategory") val categoryName: String?,
    @Json(name = "company") val company: String?,
    @Json(name = "route") val route: String?,
    @Json(name = "description") val description: String?,
)

@JsonClass(generateAdapter = true)
data class ProductsPageDto(
    @Json(name = "content") val content: List<ProductDto>,
    @Json(name = "pageNumber") val pageNumber: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalElements") val totalElements: Int,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "last") val last: Boolean
)
