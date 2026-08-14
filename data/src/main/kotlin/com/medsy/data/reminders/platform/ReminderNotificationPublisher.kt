package com.medsy.data.reminders.platform

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.medsy.data.R
import com.medsy.data.reminders.local.MedicationReminderEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderNotificationPublisher @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun canPostNotifications(): Boolean {
        val runtimePermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return runtimePermissionGranted && NotificationManagerCompat.from(context)
            .areNotificationsEnabled()
    }

    fun post(entity: MedicationReminderEntity, timeIndex: Int) {
        if (!canPostNotifications()) return
        createChannel()

        val publicNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.reminder_notification_public_title))
            .setContentText(context.getString(R.string.reminder_notification_public_body))
            .build()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.reminder_notification_title))
            .setContentText(
                context.getString(R.string.reminder_notification_body, entity.medicineName)
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicNotification)
            .setContentIntent(openRemindersIntent())
            .build()
            .apply { flags = flags or Notification.FLAG_INSISTENT }

        NotificationManagerCompat.from(context).notify(notificationId(entity.id, timeIndex), notification)
    }

    fun createChannel() {
        val alarmAudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.reminder_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.reminder_notification_channel_description)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            enableVibration(true)
            vibrationPattern = ALARM_VIBRATION_PATTERN
            setSound(alarmSound, alarmAudioAttributes)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun cancel(entity: MedicationReminderEntity) {
        val notificationManager = NotificationManagerCompat.from(context)
        repeat(entity.timesCsv.split(',').size) { timeIndex ->
            notificationManager.cancel(notificationId(entity.id, timeIndex))
        }
    }

    private fun openRemindersIntent(): PendingIntent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        launchIntent.action = ReminderAlarmScheduler.ACTION_OPEN_REMINDERS
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context,
            OPEN_REMINDERS_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun notificationId(reminderId: Long, timeIndex: Int): Int =
        ((reminderId xor (reminderId ushr 32)) * 37L + timeIndex).toInt()

    private companion object {
        const val CHANNEL_ID = "medication_alarm_reminders_v2"
        const val OPEN_REMINDERS_REQUEST_CODE = 7_201
        val ALARM_VIBRATION_PATTERN = longArrayOf(0L, 500L, 300L, 500L, 300L, 800L)
    }
}
