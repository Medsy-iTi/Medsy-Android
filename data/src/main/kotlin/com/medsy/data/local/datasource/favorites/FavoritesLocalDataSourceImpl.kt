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

    override fun getAllFavorites(): Flow<List<FavoriteProductEntity>> {
        return favoriteProductDao.getAllFavorites()
    }

    override suspend fun addFavorite(entity: FavoriteProductEntity) {
        favoriteProductDao.insertFavorite(entity)
    }

    override suspend fun removeFavorite(productId: Int) {
        favoriteProductDao.deleteFavoriteById(productId)
    }

    override suspend fun isFavorite(productId: Int): Boolean {
        return favoriteProductDao.isFavorite(productId)
    }
}
