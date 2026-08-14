package com.medsy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.medsy.data.local.database.dao.FavoriteProductDao
import com.medsy.data.local.database.entity.FavoriteProductEntity
import com.medsy.data.reminders.local.MedicationReminderDao
import com.medsy.data.reminders.local.MedicationReminderEntity

@Database(
    entities = [FavoriteProductEntity::class, MedicationReminderEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class MedsyDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
    abstract fun medicationReminderDao(): MedicationReminderDao
}
