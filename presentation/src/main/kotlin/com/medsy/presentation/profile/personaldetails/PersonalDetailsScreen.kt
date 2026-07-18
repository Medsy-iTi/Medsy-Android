package com.medsy.presentation.profile.personaldetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.domain.profile.model.Profile
import com.medsy.presentation.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun PersonalDetailsRoot(
    onNavigateBack: () -> Unit,
    viewModel: PersonalDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val saveSuccessMessage = stringResource(R.string.profile_personal_details_save_success)

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            val message = when (effect) {
                PersonalDetailsUIEffect.SaveSucceeded -> saveSuccessMessage
                is PersonalDetailsUIEffect.SaveFailed -> context.getString(effect.messageRes)
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    BackHandler(enabled = state.isEditing) {
        viewModel.onIntent(PersonalDetailsUIIntent.CancelEditClicked)
        onNavigateBack()
    }

    PersonalDetailsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = {
            if (state.isEditing) {
                viewModel.onIntent(PersonalDetailsUIIntent.CancelEditClicked)
            }
            onNavigateBack()
        },
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailsScreen(
    state: PersonalDetailsState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onIntent: (PersonalDetailsUIIntent) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_personal_details_title),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                        )
                    }
                },
                actions = {
                    if (state.profile != null && !state.isLoading) {
                        TextButton(
                            onClick = {
                                onIntent(
                                    if (state.isEditing) {
                                        PersonalDetailsUIIntent.CancelEditClicked
                                    } else {
                                        PersonalDetailsUIIntent.EditClicked
                                    }
                                )
                            },
                            enabled = !state.isSaving,
                        ) {
                            Text(
                                text = stringResource(
                                    if (state.isEditing) {
                                        R.string.profile_personal_details_cancel
                                    } else {
                                        R.string.profile_personal_details_edit
                                    }
                                )
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            state.hasLoadError || state.profile == null -> ErrorContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                messageRes = state.loadErrorMessageRes,
                onRetry = { onIntent(PersonalDetailsUIIntent.Retry) },
            )

            else -> PersonalDetailsContent(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onAddressChanged = {
                    onIntent(PersonalDetailsUIIntent.HomeAddressChanged(it))
                },
                onDatePickerClick = { showDatePicker = true },
                onSaveClick = { onIntent(PersonalDetailsUIIntent.SaveClicked) },
            )
        }
    }

    if (showDatePicker) {
        PersonalDetailsDatePicker(
            currentDate = state.draftDob,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                onIntent(PersonalDetailsUIIntent.DobSelected(it))
                showDatePicker = false
            },
        )
    }
}

@Composable
private fun PersonalDetailsContent(
    state: PersonalDetailsState,
    modifier: Modifier = Modifier,
    onAddressChanged: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val profile = requireNotNull(state.profile)
    val notProvided = stringResource(R.string.profile_personal_details_not_provided)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 20.dp,
            end = 20.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.profile_avatar_content_description),
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        item {
            PersonalDetailsInfoContainer(
                label = stringResource(R.string.profile_personal_details_customer_id),
                value = profile.id?.toString().orEmpty().orNotProvided(notProvided),
            )
        }
        item {
            PersonalDetailsInfoContainer(
                label = stringResource(R.string.profile_personal_details_first_name),
                value = profile.firstName.orNotProvided(notProvided),
            )
        }
        item {
            PersonalDetailsInfoContainer(
                label = stringResource(R.string.profile_personal_details_last_name),
                value = profile.lastName.orNotProvided(notProvided),
            )
        }
        item {
            PersonalDetailsInfoContainer(
                label = stringResource(R.string.profile_personal_details_email),
                value = profile.email.orNotProvided(notProvided),
            )
        }
        item {
            PersonalDetailsInfoContainer(
                label = stringResource(R.string.profile_personal_details_phone),
                value = profile.phoneNumber.orNotProvided(notProvided),
            )
        }
        item {
            if (state.isEditing) {
                PersonalDetailsEditableField(
                    label = stringResource(R.string.profile_personal_details_home_address),
                    value = state.draftHomeAddress,
                    placeholder = notProvided,
                    onValueChange = onAddressChanged,
                    singleLine = false,
                    minLines = 3,
                )
            } else {
                PersonalDetailsInfoContainer(
                    label = stringResource(R.string.profile_personal_details_home_address),
                    value = profile.homeAddress.orEmpty().orNotProvided(notProvided),
                    editable = true,
                )
            }
        }
        item {
            if (state.isEditing) {
                PersonalDetailsEditableField(
                    label = stringResource(R.string.profile_personal_details_dob),
                    value = state.draftDob,
                    placeholder = notProvided,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = onDatePickerClick) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = stringResource(
                                    R.string.profile_personal_details_select_dob
                                ),
                            )
                        }
                    },
                )
            } else {
                PersonalDetailsInfoContainer(
                    label = stringResource(R.string.profile_personal_details_dob),
                    value = profile.dob.orEmpty().orNotProvided(notProvided),
                    editable = true,
                )
            }
        }
        item {
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                enabled = state.isEditing && state.hasChanges && !state.isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.profile_personal_details_save),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalDetailsInfoContainer(
    label: String,
    value: String,
    editable: Boolean = false,
) {
    val borderColor = if (editable) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor,
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (editable) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.profile_personal_details_editable
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun PersonalDetailsEditableField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = singleLine,
            minLines = minLines,
            trailingIcon = trailingIcon,
            placeholder = {
                Text(text = placeholder)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalDetailsDatePicker(
    currentDate: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
) {
    val initialDateMillis = remember(currentDate) {
        runCatching {
            LocalDate.parse(currentDate)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                        onDateSelected(selectedDate)
                    }
                },
                enabled = datePickerState.selectedDateMillis != null,
            ) {
                Text(text = stringResource(R.string.profile_personal_details_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.profile_personal_details_cancel))
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    messageRes: Int?,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    val displayedMessage = stringResource(
        messageRes ?: R.string.profile_personal_details_load_error
    )

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = displayedMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.profile_retry))
        }
    }
}

private fun String.orNotProvided(notProvided: String): String =
    ifBlank { notProvided }

@Preview
@Composable
private fun PersonalDetailsScreenPreview() {
    PersonalDetailsScreen(
        state = PersonalDetailsState(
            profile = Profile(
                id = 12,
                email = "customer@example.com",
                firstName = "Mahmoud",
                lastName = "Eldemerdash",
                homeAddress = "Cairo, Egypt",
                dob = "2000-01-01",
                phoneNumber = "+20 100 000 0000",
            ),
            isLoading = false,
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onNavigateBack = {},
        onIntent = {},
    )
}
