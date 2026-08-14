package com.medsy.presentation.reminders

import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun ManualReminderDialog(
    draft: ManualReminderDraft,
    isSaving: Boolean,
    onIntent: (MedicationRemindersUIIntent) -> Unit,
) {
    val context = LocalContext.current
    val timeFormatter = remember(Locale.getDefault()) {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
    }

    fun showTimePicker() {
        val initialTime = draft.times.lastOrNull()
            ?: LocalTime.now(ZoneId.of("Africa/Cairo"))
                .plusHours(1)
                .withMinute(0)
        TimePickerDialog(
            context,
            { _, hour, minute ->
                onIntent(MedicationRemindersUIIntent.ManualTimeAdded(LocalTime.of(hour, minute)))
            },
            initialTime.hour,
            initialTime.minute,
            DateFormat.is24HourFormat(context),
        ).show()
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) onIntent(MedicationRemindersUIIntent.AddReminderDismissed)
        },
        title = { Text(stringResource(R.string.reminder_add_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OutlinedTextField(
                    value = draft.medicineName,
                    onValueChange = {
                        onIntent(MedicationRemindersUIIntent.ManualMedicineNameChanged(it))
                    },
                    label = { Text(stringResource(R.string.reminder_medicine_name_label)) },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.reminder_times_label),
                    style = MaterialTheme.typography.titleSmall,
                )
                if (draft.times.isEmpty()) {
                    Text(
                        text = stringResource(R.string.reminder_no_times_added),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    draft.times.forEach { time ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = time.format(timeFormatter),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                enabled = !isSaving,
                                onClick = {
                                    onIntent(MedicationRemindersUIIntent.ManualTimeRemoved(time))
                                },
                            ) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = stringResource(
                                        R.string.reminder_remove_time_description,
                                        time.format(timeFormatter),
                                    ),
                                )
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = ::showTimePicker,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Text(
                        text = stringResource(R.string.reminder_add_time),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Text(
                    text = stringResource(R.string.reminder_duration_label),
                    style = MaterialTheme.typography.titleSmall,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        enabled = !isSaving && draft.durationDays > 1,
                        onClick = {
                            onIntent(
                                MedicationRemindersUIIntent.ManualDurationChanged(
                                    draft.durationDays - 1
                                )
                            )
                        },
                    ) {
                        Icon(
                            Icons.Outlined.Remove,
                            contentDescription = stringResource(R.string.reminder_decrease_duration),
                        )
                    }
                    Text(
                        text = pluralStringResource(
                            R.plurals.reminder_duration_days,
                            draft.durationDays,
                            draft.durationDays,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    IconButton(
                        enabled = !isSaving && draft.durationDays < 90,
                        onClick = {
                            onIntent(
                                MedicationRemindersUIIntent.ManualDurationChanged(
                                    draft.durationDays + 1
                                )
                            )
                        },
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.reminder_increase_duration),
                        )
                    }
                }

                draft.errorMessageRes?.let { messageRes ->
                    Text(
                        text = stringResource(messageRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = { onIntent(MedicationRemindersUIIntent.SaveManualReminderClicked) },
            ) {
                Text(
                    stringResource(
                        if (isSaving) R.string.reminder_saving else R.string.reminder_save
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isSaving,
                onClick = { onIntent(MedicationRemindersUIIntent.AddReminderDismissed) },
            ) {
                Text(stringResource(R.string.reminder_cancel))
            }
        },
    )
}
