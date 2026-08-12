package com.medsy.data.favorites.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteProductDto(
    val id: Int,
    val name: String,
    val arabicName: String,
    val scientificName: String,
    val price: Double,
    val imageUrl: String?,
    val categoryId: Int,
    val categoryName: String,
    val company: String,
    val route: String
)
