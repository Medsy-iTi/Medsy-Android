package com.medsy.data.local.datasource.favorites

import com.medsy.data.local.database.entity.FavoriteProductEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesLocalDataSource {
    fun getAllFavorites(userId: Long): Flow<List<FavoriteProductEntity>>
    suspend fun addFavorite(entity: FavoriteProductEntity)
    suspend fun removeFavorite(userId: Long, productId: Int)
    suspend fun isFavorite(userId: Long, productId: Int): Boolean
}
