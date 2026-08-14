package com.medsy.data.reminders.platform

import com.medsy.data.local.auth.TokenStorage
import com.medsy.data.reminders.local.MedicationReminderDao
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderBroadcastHandler @Inject constructor(
    private val dao: MedicationReminderDao,
    private val tokenStorage: TokenStorage,
    private val scheduler: ReminderAlarmScheduler,
    private val notificationPublisher: ReminderNotificationPublisher,
) {
    suspend fun deliver(reminderId: Long, timeIndex: Int) {
        val entity = dao.getById(reminderId) ?: return
        val currentUserId = tokenStorage.readSession()?.user?.id ?: return
        if (entity.userId != currentUserId || !entity.isActive) return

        val now = Instant.now()
        if (!now.isBefore(Instant.ofEpochMilli(entity.expiresAtEpochMillis))) {
            dao.markInactive(entity.id)
            scheduler.cancel(entity)
            notificationPublisher.cancel(entity)
            return
        }

        notificationPublisher.post(entity, timeIndex)
        scheduler.scheduleNext(entity, timeIndex, now.plusSeconds(1))
    }

    suspend fun rescheduleCurrentUser() {
        val userId = tokenStorage.readSession()?.user?.id ?: return
        val now = System.currentTimeMillis()
        dao.markExpired(now)
        dao.getAllExcept(userId).forEach { entity ->
            scheduler.cancel(entity)
            notificationPublisher.cancel(entity)
        }
        dao.getActive(userId, now).forEach(scheduler::schedule)
    }
}
