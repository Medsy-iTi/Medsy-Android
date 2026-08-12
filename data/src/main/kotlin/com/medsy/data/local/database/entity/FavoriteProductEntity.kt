package com.medsy.data.local.database.entity

import androidx.room.Entity

@Entity(
    tableName = "favorite_products",
    primaryKeys = ["userId", "id"]
)
data class FavoriteProductEntity(
    val userId: Long,
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
