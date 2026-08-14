package com.medsy.presentation.reminders

import android.Manifest
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showMessage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.reminders.model.MedicationReminder
import com.medsy.presentation.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun MedicationRemindersRoot(
    onNavigateBack: () -> Unit,
    viewModel: MedicationRemindersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onIntent(MedicationRemindersUIIntent.NotificationPermissionResult(granted))
    }

    fun refreshCapabilities() {
        val notificationsEnabled =
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED) &&
                NotificationManagerCompat.from(context).areNotificationsEnabled()
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val exactEnabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()
        viewModel.onIntent(
            MedicationRemindersUIIntent.ScreenResumed(notificationsEnabled, exactEnabled)
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshCapabilities()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        refreshCapabilities()
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MedicationRemindersUIEffect.NavigateBack -> onNavigateBack()
                MedicationRemindersUIEffect.RequestNotificationPermission -> {
                    val runtimePermissionGranted =
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) == PackageManager.PERMISSION_GRANTED
                    if (!runtimePermissionGranted &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                        context.startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        )
                    } else {
                        viewModel.onIntent(
                            MedicationRemindersUIIntent.NotificationPermissionResult(true)
                        )
                    }
                }

                MedicationRemindersUIEffect.OpenExactAlarmSettings -> try {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:${context.packageName}"),
                        )
                    )
                } catch (_: ActivityNotFoundException) {
                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                }

                MedicationRemindersUIEffect.OpenBatteryOptimizationSettings -> try {
                    context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                } catch (_: ActivityNotFoundException) {
                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                }

                is MedicationRemindersUIEffect.ShowMessage ->
                    snackbarHostState.showMessage(
                        context = context,
                        messageRes = effect.messageRes,
                        isSuccess = effect.isSuccess,
                    )
            }
        }
    }

    Scaffold(
        snackbarHost = { MedsySnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!state.isLoading && state.errorMessageRes == null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.onIntent(MedicationRemindersUIIntent.AddReminderClicked)
                    },
                    icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.reminder_add)) },
                )
            }
        },
        topBar = {
            RemindersTopBar { viewModel.onIntent(MedicationRemindersUIIntent.BackClicked) }
        },
    ) { padding ->
        MedicationRemindersScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(padding),
        )
    }

    state.pendingDelete?.let { reminder ->
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(MedicationRemindersUIIntent.DeleteDismissed) },
            title = { Text(stringResource(R.string.reminder_delete_title)) },
            text = {
                Text(stringResource(R.string.reminder_delete_body, reminder.medicineName))
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onIntent(MedicationRemindersUIIntent.DeleteConfirmed)
                }) { Text(stringResource(R.string.reminder_delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onIntent(MedicationRemindersUIIntent.DeleteDismissed)
                }) { Text(stringResource(R.string.ai_chat_cancel)) }
            },
        )
    }

    state.manualReminderDraft?.let { draft ->
        ManualReminderDialog(
            draft = draft,
            isSaving = state.isSavingManualReminder,
            onIntent = viewModel::onIntent,
        )
    }

    if (state.isBatteryReliabilityDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onIntent(MedicationRemindersUIIntent.BatteryReliabilityDismissed)
            },
            title = { Text(stringResource(R.string.reminder_battery_title)) },
            text = { Text(stringResource(R.string.reminder_battery_body)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onIntent(MedicationRemindersUIIntent.BatterySettingsClicked)
                }) { Text(stringResource(R.string.reminder_open_battery_settings)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onIntent(MedicationRemindersUIIntent.BatteryReliabilityDismissed)
                }) { Text(stringResource(R.string.reminder_not_now)) }
            },
        )
    }
}

@Composable
fun MedicationRemindersScreen(
    state: MedicationRemindersState,
    onIntent: (MedicationRemindersUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            state.errorMessageRes != null -> ReminderErrorState(
                message = stringResource(state.errorMessageRes),
                onRetry = { onIntent(MedicationRemindersUIIntent.RetryClicked) },
                modifier = Modifier.align(Alignment.Center),
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp,
                    bottom = 104.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                if (!state.notificationsEnabled) {
                    item("notifications_disabled") {
                        CapabilityBanner(
                            title = stringResource(R.string.reminder_notifications_off_title),
                            body = stringResource(R.string.reminder_notifications_off_body),
                            action = stringResource(R.string.reminder_enable_notifications),
                            onClick = {
                                onIntent(MedicationRemindersUIIntent.EnableNotificationsClicked)
                            },
                        )
                    }
                } else if (!state.exactAlarmsEnabled) {
                    item("exact_disabled") {
                        CapabilityBanner(
                            title = stringResource(R.string.reminder_approximate_title),
                            body = stringResource(R.string.reminder_approximate_body),
                            action = stringResource(R.string.reminder_enable_exact_alarms),
                            onClick = {
                                onIntent(MedicationRemindersUIIntent.EnableExactAlarmsClicked)
                            },
                        )
                    }
                }

                if (state.isEmpty) {
                    item("empty") { ReminderEmptyState() }
                } else {
                    items(state.reminders, key = MedicationReminder::id) { reminder ->
                        ReminderListItem(
                            reminder = reminder,
                            onDelete = {
                                onIntent(MedicationRemindersUIIntent.DeleteClicked(reminder.id))
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemindersTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.reminder_list_title), fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_back),
                )
            }
        },
    )
}

@Composable
private fun ReminderListItem(reminder: MedicationReminder, onDelete: () -> Unit) {
    val timeSeparator = stringResource(R.string.reminder_time_separator)
    val timeFormatter = remember {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
    }
    val dateFormatter = remember {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
    }
    val endDate = reminder.expiresAt.atZone(ZoneId.of("Africa/Cairo")).toLocalDate()
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Alarm,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(reminder.medicineName, style = MaterialTheme.typography.titleMedium)
                Text(
                    reminder.times.joinToString(timeSeparator) { it.format(timeFormatter) },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    stringResource(
                        R.string.reminder_duration_and_end,
                        pluralStringResource(
                            R.plurals.reminder_duration_days,
                            reminder.durationDays,
                            reminder.durationDays,
                        ),
                        endDate.format(dateFormatter),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    stringResource(
                        if (reminder.isActive) R.string.reminder_active else R.string.reminder_completed
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (reminder.isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = stringResource(
                        R.string.reminder_delete_description,
                        reminder.medicineName,
                    ),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun CapabilityBanner(
    title: String,
    body: String,
    action: String,
    onClick: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.warningContainer,
        ),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(action) }
        }
    }
}

@Composable
private fun ReminderEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            Icons.Outlined.Alarm,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(stringResource(R.string.reminder_empty_title), fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.reminder_empty_body),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReminderErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Outlined.NotificationsOff, contentDescription = null, modifier = Modifier.size(48.dp))
        Text(message, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text(stringResource(R.string.ai_chat_catalog_retry)) }
    }
}
