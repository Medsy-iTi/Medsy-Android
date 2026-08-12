package com.medsy.data.favorites.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.medsy.data.favorites.mapper.toDomain
import com.medsy.data.favorites.mapper.toDto
import com.medsy.data.favorites.mapper.toEntity
import com.medsy.data.local.database.entity.FavoriteProductEntity
import com.medsy.data.local.datasource.favorites.FavoritesLocalDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.favorites.model.FavoriteProduct
import com.medsy.domain.favorites.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesLocalDataSource: FavoritesLocalDataSource
) : FavoritesRepository {

    override fun getAllFavorites(userId: Long): Flow<MedsyResult<List<FavoriteProduct>, MedsyError.Local>> {
        return favoritesLocalDataSource.getAllFavorites(userId)
            .map<List<FavoriteProductEntity>, MedsyResult<List<FavoriteProduct>, MedsyError.Local>> { entities ->
                MedsyResult.Success(entities.map { it.toDto().toDomain() })
            }
            .catch { throwable ->
                emit(MedsyResult.Error(throwable.toLocalError()))
            }
    }

    override suspend fun addFavorite(product: FavoriteProduct, userId: Long): EmptyMedsyResult<MedsyError.Local> {
        return try {
            favoritesLocalDataSource.addFavorite(product.toDto().toEntity(userId))
            MedsyResult.Success(Unit)
        } catch (e: Exception) {
            MedsyResult.Error(e.toLocalError())
        }
    }

    override suspend fun removeFavorite(productId: Int, userId: Long): EmptyMedsyResult<MedsyError.Local> {
        return try {
            favoritesLocalDataSource.removeFavorite(userId, productId)
            MedsyResult.Success(Unit)
        } catch (e: Exception) {
            MedsyResult.Error(e.toLocalError())
        }
    }

    override suspend fun isFavorite(productId: Int, userId: Long): MedsyResult<Boolean, MedsyError.Local> {
        return try {
            val exists = favoritesLocalDataSource.isFavorite(userId, productId)
            MedsyResult.Success(exists)
        } catch (e: Exception) {
            MedsyResult.Error(e.toLocalError())
        }
    }

    private fun Throwable.toLocalError(): MedsyError.Local = when (this) {
        is SQLiteConstraintException -> MedsyError.Local.DATABASE_CONSTRAINT
        is SQLiteFullException -> MedsyError.Local.DATABASE_FULL
        is SQLiteDatabaseCorruptException -> MedsyError.Local.DATABASE_CORRUPT
        is SQLiteDiskIOException, is IOException -> MedsyError.Local.DATABASE_IO
        is SQLiteException -> MedsyError.Local.DATABASE_ERROR
        else -> MedsyError.Local.UNKNOWN
    }
}
