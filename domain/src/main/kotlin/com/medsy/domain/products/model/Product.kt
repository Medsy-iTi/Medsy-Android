package com.medsy.domain.products.model

data class Product(
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
