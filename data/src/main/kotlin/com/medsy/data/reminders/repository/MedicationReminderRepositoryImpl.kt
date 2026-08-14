package com.medsy.data.reminders.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.medsy.data.reminders.local.MedicationReminderDao
import com.medsy.data.reminders.local.MedicationReminderEntity
import com.medsy.data.reminders.local.ReminderPromptPreferences
import com.medsy.data.reminders.mapper.toCsv
import com.medsy.data.reminders.mapper.toDomain
import com.medsy.data.reminders.platform.ReminderAlarmScheduler
import com.medsy.data.reminders.platform.ReminderNotificationPublisher
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.reminders.model.CreateMedicationReminderParams
import com.medsy.domain.reminders.model.MedicationReminder
import com.medsy.domain.reminders.model.ReminderScheduleStatus
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CancellationException

@Singleton
class MedicationReminderRepositoryImpl @Inject constructor(
    private val dao: MedicationReminderDao,
    private val scheduler: ReminderAlarmScheduler,
    private val notificationPublisher: ReminderNotificationPublisher,
    private val promptPreferences: ReminderPromptPreferences,
) : MedicationReminderRepository {

    override fun observe(
        userId: Long,
    ): Flow<MedsyResult<List<MedicationReminder>, MedsyError.Local>> = flow {
        dao.markExpired(System.currentTimeMillis())
        emitAll(
            dao.observeByUser(userId).map<
                List<MedicationReminderEntity>,
                MedsyResult<List<MedicationReminder>, MedsyError.Local>
            > { entities ->
                MedsyResult.Success(entities.map(MedicationReminderEntity::toDomain))
            }
        )
    }.catch { throwable ->
        throwable.rethrowCancellation()
        emit(MedsyResult.Error(throwable.toLocalError()))
    }

    override suspend fun create(
        userId: Long,
        params: CreateMedicationReminderParams,
    ): MedsyResult<MedicationReminder, MedsyError.Local> {
        return try {
            params.sourceMessageId?.let { sourceMessageId ->
                dao.getBySourceMessageId(userId, sourceMessageId)?.let { existing ->
                    return MedsyResult.Success(existing.toDomain())
                }
            }

            val createdAt = Instant.now()
            val expiresAt = createdAt
                .atZone(ReminderAlarmScheduler.CAIRO_ZONE)
                .plusDays(params.durationDays.toLong())
                .toInstant()
            val entity = MedicationReminderEntity(
                userId = userId,
                sourceMessageId = params.sourceMessageId,
                medicineName = params.medicineName,
                timesCsv = params.times.toCsv(),
                durationDays = params.durationDays,
                createdAtEpochMillis = createdAt.toEpochMilli(),
                expiresAtEpochMillis = expiresAt.toEpochMilli(),
                isActive = true,
            )
            val insertedId = dao.insert(entity)
            val saved = if (insertedId > 0L) {
                entity.copy(id = insertedId)
            } else {
                params.sourceMessageId
                    ?.let { dao.getBySourceMessageId(userId, it) }
                    ?: return MedsyResult.Error(MedsyError.Local.DATABASE_CONSTRAINT)
            }
            MedsyResult.Success(saved.toDomain())
        } catch (throwable: Throwable) {
            throwable.rethrowCancellation()
            MedsyResult.Error(throwable.toLocalError())
        }
    }

    override suspend fun schedule(
        userId: Long,
        reminderId: Long,
    ): MedsyResult<ReminderScheduleStatus, MedsyError.Local> {
        return try {
            dao.markExpired(System.currentTimeMillis())
            val entity = dao.getById(reminderId, userId)
                ?: return MedsyResult.Success(ReminderScheduleStatus.EXPIRED_OR_MISSING)
            if (!entity.isActive || entity.expiresAtEpochMillis <= System.currentTimeMillis()) {
                scheduler.cancel(entity)
                return MedsyResult.Success(ReminderScheduleStatus.EXPIRED_OR_MISSING)
            }
            if (!notificationPublisher.canPostNotifications()) {
                return MedsyResult.Success(ReminderScheduleStatus.NOTIFICATIONS_DISABLED)
            }
            val exact = scheduler.schedule(entity)
            MedsyResult.Success(
                if (exact) ReminderScheduleStatus.EXACT else ReminderScheduleStatus.INEXACT
            )
        } catch (throwable: Throwable) {
            throwable.rethrowCancellation()
            MedsyResult.Error(throwable.toLocalError())
        }
    }

    override suspend fun delete(
        userId: Long,
        reminderId: Long,
    ): EmptyMedsyResult<MedsyError.Local> = try {
        dao.getById(reminderId, userId)?.let { entity ->
            scheduler.cancel(entity)
            notificationPublisher.cancel(entity)
        }
        dao.delete(reminderId, userId)
        MedsyResult.Success(Unit)
    } catch (throwable: Throwable) {
        throwable.rethrowCancellation()
        MedsyResult.Error(throwable.toLocalError())
    }

    override suspend fun rescheduleActive(
        userId: Long,
    ): EmptyMedsyResult<MedsyError.Local> = try {
        val now = System.currentTimeMillis()
        dao.markExpired(now)
        dao.getAllExcept(userId).forEach { entity ->
            scheduler.cancel(entity)
            notificationPublisher.cancel(entity)
        }
        if (notificationPublisher.canPostNotifications()) {
            dao.getActive(userId, now).forEach(scheduler::schedule)
        }
        MedsyResult.Success(Unit)
    } catch (throwable: Throwable) {
        throwable.rethrowCancellation()
        MedsyResult.Error(throwable.toLocalError())
    }

    override suspend fun cancelScheduled(
        userId: Long,
    ): EmptyMedsyResult<MedsyError.Local> = try {
        dao.getAll(userId).forEach { entity ->
            scheduler.cancel(entity)
            notificationPublisher.cancel(entity)
        }
        MedsyResult.Success(Unit)
    } catch (throwable: Throwable) {
        throwable.rethrowCancellation()
        MedsyResult.Error(throwable.toLocalError())
    }

    override suspend fun consumeBatteryReliabilityPrompt(): Boolean =
        promptPreferences.consumeBatteryReliabilityPrompt()

    override suspend fun consumeNotificationPermissionPrompt(): Boolean =
        promptPreferences.consumeNotificationPermissionPrompt()

    private fun Throwable.toLocalError(): MedsyError.Local = when (this) {
        is SQLiteConstraintException -> MedsyError.Local.DATABASE_CONSTRAINT
        is SQLiteFullException -> MedsyError.Local.DATABASE_FULL
        is SQLiteDatabaseCorruptException -> MedsyError.Local.DATABASE_CORRUPT
        is SQLiteDiskIOException, is IOException -> MedsyError.Local.DATABASE_IO
        is SQLiteException -> MedsyError.Local.DATABASE_ERROR
        else -> MedsyError.Local.UNKNOWN
    }

    private fun Throwable.rethrowCancellation() {
        if (this is CancellationException) throw this
    }
}
