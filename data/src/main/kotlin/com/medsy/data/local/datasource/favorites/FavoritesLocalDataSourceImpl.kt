package com.medsy.data.local.datasource.favorites

import com.medsy.data.local.database.dao.FavoriteProductDao
import com.medsy.data.local.database.entity.FavoriteProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesLocalDataSourceImpl @Inject constructor(
    private val favoriteProductDao: FavoriteProductDao
) : FavoritesLocalDataSource {

    override fun getAllFavorites(userId: Long): Flow<List<FavoriteProductEntity>> {
        return favoriteProductDao.getAllFavorites(userId)
    }

    override suspend fun addFavorite(entity: FavoriteProductEntity) {
        favoriteProductDao.insertFavorite(entity)
    }

    override suspend fun removeFavorite(userId: Long, productId: Int) {
        favoriteProductDao.deleteFavoriteById(userId, productId)
    }

    override suspend fun isFavorite(userId: Long, productId: Int): Boolean {
        return favoriteProductDao.isFavorite(userId, productId)
    }
}
