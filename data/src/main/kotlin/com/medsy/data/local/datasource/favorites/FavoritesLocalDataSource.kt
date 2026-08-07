package com.medsy.data.local.datasource.favorites

import com.medsy.data.local.database.entity.FavoriteProductEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesLocalDataSource {
    fun getAllFavorites(): Flow<List<FavoriteProductEntity>>
    suspend fun addFavorite(entity: FavoriteProductEntity)
    suspend fun removeFavorite(productId: Int)
    suspend fun isFavorite(productId: Int): Boolean
}
