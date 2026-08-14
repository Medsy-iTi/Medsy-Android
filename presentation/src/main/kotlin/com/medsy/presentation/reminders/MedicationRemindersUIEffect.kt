package com.medsy.presentation.reminders

import androidx.annotation.StringRes

sealed interface MedicationRemindersUIEffect {
    data object NavigateBack : MedicationRemindersUIEffect
    data object RequestNotificationPermission : MedicationRemindersUIEffect
    data object OpenExactAlarmSettings : MedicationRemindersUIEffect
    data object OpenBatteryOptimizationSettings : MedicationRemindersUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val isSuccess: Boolean = false,
    ) : MedicationRemindersUIEffect
}
