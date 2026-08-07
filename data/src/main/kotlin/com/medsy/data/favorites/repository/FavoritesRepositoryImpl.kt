package com.medsy.data.favorites.repository

import com.medsy.data.local.database.entity.FavoriteProductEntity
import com.medsy.data.local.datasource.favorites.FavoritesLocalDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.favorites.repository.FavoritesRepository
import com.medsy.domain.search.model.SearchProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesLocalDataSource: FavoritesLocalDataSource
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<SearchProduct>> {
        return favoritesLocalDataSource.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(product: SearchProduct): EmptyMedsyResult<MedsyError.Local> {
        return try {
            favoritesLocalDataSource.addFavorite(FavoriteProductEntity.fromDomain(product))
            MedsyResult.Success(Unit)
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }

    override suspend fun removeFavorite(productId: Int): EmptyMedsyResult<MedsyError.Local> {
        return try {
            favoritesLocalDataSource.removeFavorite(productId)
            MedsyResult.Success(Unit)
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }

    override suspend fun isFavorite(productId: Int): MedsyResult<Boolean, MedsyError.Local> {
        return try {
            val exists = favoritesLocalDataSource.isFavorite(productId)
            MedsyResult.Success(exists)
        } catch (e: Exception) {
            MedsyResult.Error(MedsyError.Local.UNKNOWN)
        }
    }
}
