package com.medsy.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS `medication_reminders` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `userId` INTEGER NOT NULL,
                `sourceMessageId` INTEGER,
                `medicineName` TEXT NOT NULL,
                `timesCsv` TEXT NOT NULL,
                `durationDays` INTEGER NOT NULL,
                `createdAtEpochMillis` INTEGER NOT NULL,
                `expiresAtEpochMillis` INTEGER NOT NULL,
                `isActive` INTEGER NOT NULL
            )""".trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_medication_reminders_userId` " +
                "ON `medication_reminders` (`userId`)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_medication_reminders_userId_sourceMessageId` " +
                "ON `medication_reminders` (`userId`, `sourceMessageId`)"
        )
    }
}
