package com.medsy.data.productdetails.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetailsResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: ProductDetailsDto
)

@JsonClass(generateAdapter = true)
data class ProductDetailsDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "productName") val productName: String?,
    @Json(name = "strength") val strength: String?,
    @Json(name = "packSize") val packSize: String?,
    @Json(name = "form") val form: String?,
    @Json(name = "price") val price: Double,
    @Json(name = "scientificName") val scientificName: String,
    @Json(name = "scientificCategory") val scientificCategory: String?,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "consumerCategory") val consumerCategory: String?,
    @Json(name = "company") val company: String,
    @Json(name = "route") val route: String,
    @Json(name = "description") val description: String?,
    @Json(name = "imageUrl") val imageUrl: String?
)