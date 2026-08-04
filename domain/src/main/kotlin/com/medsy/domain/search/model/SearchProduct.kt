package com.medsy.domain.search.model

data class SearchProduct(
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
