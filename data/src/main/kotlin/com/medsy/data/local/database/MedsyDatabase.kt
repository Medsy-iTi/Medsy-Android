package com.medsy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.medsy.data.local.database.dao.FavoriteProductDao
import com.medsy.data.local.database.entity.FavoriteProductEntity

@Database(entities = [FavoriteProductEntity::class], version = 2, exportSchema = false)
abstract class MedsyDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
}
