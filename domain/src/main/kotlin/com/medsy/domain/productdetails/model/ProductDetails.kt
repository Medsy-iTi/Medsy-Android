package com.medsy.domain.productdetails.model

data class ProductDetails(
    val id: Int,
    val name: String,
    val scientificName: String,
    val price: Double,
    val imageUrl: String,
    val categoryId: Int,
    val categoryName: String,
    val company: String,
    val route: String
)
