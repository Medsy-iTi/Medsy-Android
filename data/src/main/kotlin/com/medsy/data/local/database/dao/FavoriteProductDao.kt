package com.medsy.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.medsy.data.local.database.entity.FavoriteProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteProductDao {
    @Query("SELECT * FROM favorite_products WHERE userId = :userId")
    fun getAllFavorites(userId: Long): Flow<List<FavoriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteProduct: FavoriteProductEntity): Long

    @Query("DELETE FROM favorite_products WHERE userId = :userId AND id = :id")
    suspend fun deleteFavoriteById(userId: Long, id: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE userId = :userId AND id = :id)")
    suspend fun isFavorite(userId: Long, id: Int): Boolean
}
