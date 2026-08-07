package com.medsy.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.medsy.data.local.database.entity.FavoriteProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteProductDao {
    @Query("SELECT * FROM favorite_products")
    fun getAllFavorites(): Flow<List<FavoriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteProduct: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}
