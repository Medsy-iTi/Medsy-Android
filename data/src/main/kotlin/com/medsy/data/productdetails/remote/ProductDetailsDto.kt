package com.medsy.data.productdetails.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetailsDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "scientificName") val scientificName: String,
    @Json(name = "price") val price: Double,
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "categoryName") val categoryName: String,
    @Json(name = "company") val company: String,
    @Json(name = "route") val route: String
)
