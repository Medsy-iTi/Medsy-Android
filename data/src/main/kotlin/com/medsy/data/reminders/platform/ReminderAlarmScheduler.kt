package com.medsy.data.reminders.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.medsy.data.reminders.local.MedicationReminderEntity
import com.medsy.data.reminders.mapper.TIME_FORMAT
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationPublisher: ReminderNotificationPublisher,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(entity: MedicationReminderEntity): Boolean {
        cancel(entity)
        if (!notificationPublisher.canPostNotifications()) return false

        val now = Instant.now()
        val expiresAt = Instant.ofEpochMilli(entity.expiresAtEpochMillis)
        if (!entity.isActive || !now.isBefore(expiresAt)) return false

        val exact = canScheduleExactAlarms()
        entity.times().forEachIndexed { index, time ->
            nextOccurrence(time, now, expiresAt)?.let { occurrence ->
                scheduleAt(entity.id, index, occurrence, exact)
            }
        }
        return exact
    }

    fun scheduleNext(
        entity: MedicationReminderEntity,
        timeIndex: Int,
        after: Instant = Instant.now(),
    ) {
        val time = entity.times().getOrNull(timeIndex) ?: return
        val expiresAt = Instant.ofEpochMilli(entity.expiresAtEpochMillis)
        nextOccurrence(time, after, expiresAt)?.let { occurrence ->
            scheduleAt(entity.id, timeIndex, occurrence, canScheduleExactAlarms())
        }
    }

    fun cancel(entity: MedicationReminderEntity) {
        repeat(entity.times().size) { index ->
            pendingIntent(entity.id, index, PendingIntent.FLAG_NO_CREATE)?.let(alarmManager::cancel)
        }
    }

    fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    private fun scheduleAt(
        reminderId: Long,
        timeIndex: Int,
        occurrence: Instant,
        exact: Boolean,
    ) {
        val operation = pendingIntent(reminderId, timeIndex, PendingIntent.FLAG_UPDATE_CURRENT)
            ?: return
        if (exact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                occurrence.toEpochMilli(),
                operation,
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                occurrence.toEpochMilli(),
                operation,
            )
        }
    }

    private fun pendingIntent(
        reminderId: Long,
        timeIndex: Int,
        lookupFlag: Int,
    ): PendingIntent? = PendingIntent.getBroadcast(
        context,
        requestCode(reminderId, timeIndex),
        Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ACTION_DELIVER_REMINDER
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_TIME_INDEX, timeIndex)
        },
        lookupFlag or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun nextOccurrence(
        time: LocalTime,
        after: Instant,
        expiresAt: Instant,
    ): Instant? {
        val afterInCairo = after.atZone(CAIRO_ZONE)
        var candidate = afterInCairo.toLocalDate().atTime(time).atZone(CAIRO_ZONE).toInstant()
        if (!candidate.isAfter(after)) {
            candidate = afterInCairo.toLocalDate().plusDays(1)
                .atTime(time)
                .atZone(CAIRO_ZONE)
                .toInstant()
        }
        return candidate.takeIf { it.isBefore(expiresAt) }
    }

    private fun MedicationReminderEntity.times(): List<LocalTime> =
        timesCsv.split(',').mapNotNull { runCatching { LocalTime.parse(it, TIME_FORMAT) }.getOrNull() }

    private fun requestCode(reminderId: Long, timeIndex: Int): Int =
        ((reminderId xor (reminderId ushr 32)) * 31L + timeIndex).toInt()

    companion object {
        val CAIRO_ZONE: ZoneId = ZoneId.of("Africa/Cairo")
        const val ACTION_DELIVER_REMINDER = "com.medsy.medsy.action.DELIVER_MEDICATION_REMINDER"
        const val ACTION_OPEN_REMINDERS = "com.medsy.medsy.action.OPEN_MEDICATION_REMINDERS"
        const val EXTRA_REMINDER_ID = "medication_reminder_id"
        const val EXTRA_TIME_INDEX = "medication_reminder_time_index"
    }
}
