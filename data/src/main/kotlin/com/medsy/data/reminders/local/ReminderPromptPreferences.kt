package com.medsy.data.reminders.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderPromptPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Synchronized
    fun consumeBatteryReliabilityPrompt(): Boolean {
        if (preferences.getBoolean(KEY_BATTERY_PROMPT_SHOWN, false)) return false
        preferences.edit { putBoolean(KEY_BATTERY_PROMPT_SHOWN, true) }
        return true
    }

    @Synchronized
    fun consumeNotificationPermissionPrompt(): Boolean {
        if (preferences.getBoolean(KEY_NOTIFICATION_PROMPT_SHOWN, false)) return false
        preferences.edit { putBoolean(KEY_NOTIFICATION_PROMPT_SHOWN, true) }
        return true
    }

    private companion object {
        const val PREFERENCES_NAME = "medsy_reminder_preferences"
        const val KEY_BATTERY_PROMPT_SHOWN = "battery_reliability_prompt_shown"
        const val KEY_NOTIFICATION_PROMPT_SHOWN = "notification_permission_prompt_shown"
    }
}
