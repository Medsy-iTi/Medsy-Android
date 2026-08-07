package com.medsy.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.medsy.domain.search.model.SearchProduct

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val arabicName: String,
    val scientificName: String,
    val price: Double,
    val imageUrl: String?,
    val categoryId: Int,
    val categoryName: String,
    val company: String,
    val route: String
) {
    fun toDomain(): SearchProduct {
        return SearchProduct(
            id = id,
            name = name,
            arabicName = arabicName,
            scientificName = scientificName,
            price = price,
            imageUrl = imageUrl,
            categoryId = categoryId,
            categoryName = categoryName,
            company = company,
            route = route
        )
    }

    companion object {
        fun fromDomain(product: SearchProduct): FavoriteProductEntity {
            return FavoriteProductEntity(
                id = product.id,
                name = product.name,
                arabicName = product.arabicName,
                scientificName = product.scientificName,
                price = product.price,
                imageUrl = product.imageUrl,
                categoryId = product.categoryId,
                categoryName = product.categoryName,
                company = product.company,
                route = product.route
            )
        }
    }
}
