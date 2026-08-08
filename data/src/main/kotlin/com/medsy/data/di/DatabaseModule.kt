package com.medsy.data.di

import android.content.Context
import androidx.room.Room
import com.medsy.data.local.database.DatabaseConstants
import com.medsy.data.local.database.MedsyDatabase
import com.medsy.data.local.database.dao.FavoriteProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMedsyDatabase(
        @ApplicationContext context: Context
    ): MedsyDatabase {
        return Room.databaseBuilder(
            context,
            MedsyDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideFavoriteProductDao(database: MedsyDatabase): FavoriteProductDao {
        return database.favoriteProductDao()
    }
}
