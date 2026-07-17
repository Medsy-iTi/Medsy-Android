package com.medsy.data.remote.dtos.products

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: Int,
    val name: String,
    val scientificName: String?,
    val price: Double,
    val imageUrl: String?,
    val categoryId: Int,
    val categoryName: String?,
    val company: String?,
    val route: String?
)
