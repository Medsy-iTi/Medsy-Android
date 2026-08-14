package com.medsy.data.reminders.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var handler: ReminderBroadcastHandler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ReminderAlarmScheduler.ACTION_DELIVER_REMINDER) return
        val reminderId = intent.getLongExtra(ReminderAlarmScheduler.EXTRA_REMINDER_ID, -1L)
        val timeIndex = intent.getIntExtra(ReminderAlarmScheduler.EXTRA_TIME_INDEX, -1)
        if (reminderId <= 0L || timeIndex < 0) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                handler.deliver(reminderId, timeIndex)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
