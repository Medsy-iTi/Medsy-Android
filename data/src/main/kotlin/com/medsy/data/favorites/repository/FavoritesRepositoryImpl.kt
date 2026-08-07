package com.medsy.data.favorites.repository

import com.medsy.data.local.database.entity.FavoriteProductEntity
import com.medsy.data.local.datasource.favorites.FavoritesLocalDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.favorites.repository.FavoritesRepository
import com.medsy.domain.favorites.model.FavoriteProduct
import com.medsy.data.favorites.mapper.toDomain
import com.medsy.data.favorites.mapper.toDto
import com.medsy.data.favorites.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesLocalDataSource: FavoritesLocalDataSource
) : FavoritesRepository {

    override fun getAllFavorites(userId: Long): Flow<List<FavoriteProduct>> {
        return favoritesLocalDataSource.getAllFavorites(userId).map { entities ->
            entities.map { it.toDto().toDomain() }
        }
    }

    override suspend fun addFavorite(product: FavoriteProduct, userId: Long): EmptyMedsyResult<MedsyError.Local> {
        return try {
            favoritesLocalDataSource.addFavorite(product.toDto().toEntity(userId))
            MedsyResult.Success(Unit)
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }

    override suspend fun removeFavorite(productId: Int, userId: Long): EmptyMedsyResult<MedsyError.Local> {
        return try {
            favoritesLocalDataSource.removeFavorite(userId, productId)
            MedsyResult.Success(Unit)
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }

    override suspend fun isFavorite(productId: Int, userId: Long): MedsyResult<Boolean, MedsyError.Local> {
        return try {
            val exists = favoritesLocalDataSource.isFavorite(userId, productId)
            MedsyResult.Success(exists)
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }
}
