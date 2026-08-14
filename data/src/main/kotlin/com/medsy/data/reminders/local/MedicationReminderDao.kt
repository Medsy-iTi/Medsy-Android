package com.medsy.data.reminders.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationReminderDao {
    @Query(
        """SELECT * FROM medication_reminders
           WHERE userId = :userId
           ORDER BY isActive DESC, createdAtEpochMillis DESC"""
    )
    fun observeByUser(userId: Long): Flow<List<MedicationReminderEntity>>

    @Query("SELECT * FROM medication_reminders WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getById(id: Long, userId: Long): MedicationReminderEntity?

    @Query("SELECT * FROM medication_reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MedicationReminderEntity?

    @Query(
        """SELECT * FROM medication_reminders
           WHERE userId = :userId AND sourceMessageId = :sourceMessageId
           LIMIT 1"""
    )
    suspend fun getBySourceMessageId(
        userId: Long,
        sourceMessageId: Long,
    ): MedicationReminderEntity?

    @Query(
        """SELECT * FROM medication_reminders
           WHERE userId = :userId AND isActive = 1 AND expiresAtEpochMillis > :nowEpochMillis"""
    )
    suspend fun getActive(userId: Long, nowEpochMillis: Long): List<MedicationReminderEntity>

    @Query("SELECT * FROM medication_reminders WHERE userId = :userId")
    suspend fun getAll(userId: Long): List<MedicationReminderEntity>

    @Query("SELECT * FROM medication_reminders WHERE userId != :userId")
    suspend fun getAllExcept(userId: Long): List<MedicationReminderEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: MedicationReminderEntity): Long

    @Query("UPDATE medication_reminders SET isActive = 0 WHERE isActive = 1 AND expiresAtEpochMillis <= :nowEpochMillis")
    suspend fun markExpired(nowEpochMillis: Long)

    @Query("UPDATE medication_reminders SET isActive = 0 WHERE id = :id")
    suspend fun markInactive(id: Long)

    @Query("DELETE FROM medication_reminders WHERE id = :id AND userId = :userId")
    suspend fun delete(id: Long, userId: Long): Int
}
