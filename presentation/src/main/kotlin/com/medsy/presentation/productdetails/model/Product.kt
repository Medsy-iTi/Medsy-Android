package com.medsy.presentation.productdetails.model

data class Product(
    val id: String,
    val name: String,
    val imageUrls: List<String>,
    val strength: String,
    val packInfo: String,
    val price: Int,
    val description: String,
    val manufacturer: String,
    val type: String,
    val category: String,
    val isFavorite: Boolean = false,
)