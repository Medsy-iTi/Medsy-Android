package com.medsy.domain.productdetails.model

data class ProductDetails(
    val id: Int,
    val name: String,
    val productName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val price: Double,
    val scientificName: String,
    val scientificCategory: String?,
    val categoryId: Int,
    val consumerCategory: String?,
    val company: String,
    val route: String,
    val description: String?,
    val imageUrl: String?
)