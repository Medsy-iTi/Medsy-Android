package com.medsy.presentation.profile.personaldetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.location.MedsyLocationPickerScreen
import com.medsy.designsystem.components.showError
import com.medsy.designsystem.components.showSuccess
import com.medsy.domain.profile.model.Profile
import com.medsy.presentation.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun PersonalDetailsRoot(
    startInEditMode: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: PersonalDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val saveSuccessMessage = stringResource(R.string.profile_personal_details_save_success)

    LaunchedEffect(startInEditMode, viewModel) {
        if (startInEditMode) {
            viewModel.onIntent(PersonalDetailsUIIntent.StartEditingAddress)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PersonalDetailsUIEffect.SaveSucceeded -> snackbarHostState.showSuccess(
                    saveSuccessMessage
                )

                is PersonalDetailsUIEffect.SaveFailed -> snackbarHostState.showError(
                    ContextCompat.getString(context, effect.messageRes)
                )
            }
        }
    }

    BackHandler(enabled = state.isEditing && !state.isMapPickerVisible) {
        viewModel.onIntent(PersonalDetailsUIIntent.CancelEditClicked)
        onNavigateBack()
    }

    if (state.isMapPickerVisible) {
        MedsyLocationPickerScreen(
            initialLatitude = state.draftLatitude,
            initialLongitude = state.draftLongitude,
            onDismiss = {
                viewModel.onIntent(PersonalDetailsUIIntent.LocationPickerDismissed)
            },
            onLocationConfirmed = { latitude, longitude ->
                viewModel.onIntent(
                    PersonalDetailsUIIntent.LocationSelected(
                        latitude = latitude,
                        longitude = longitude,
                    )
                )
            },
        )
    } else {
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
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Header Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Top app bar items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = innerPadding.calculateTopPadding() + 8.dp,
                            start = 8.dp,
                            end = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.profile_personal_details_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (state.profile != null && !state.isLoading) {
                        TextButton(
                            onClick = {
                                onIntent(
                                    if (state.isEditing) PersonalDetailsUIIntent.CancelEditClicked
                                    else PersonalDetailsUIIntent.EditClicked
                                )
                            },
                            enabled = !state.isSaving,
                        ) {
                            Text(
                                text = stringResource(
                                    if (state.isEditing) R.string.profile_personal_details_cancel
                                    else R.string.profile_personal_details_edit
                                ),
                                color = if (state.isSaving) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(64.dp))
                    }
                }

                // Avatar in Header
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 56.dp)
                        .size(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = stringResource(R.string.profile_avatar_content_description),
                            modifier = Modifier.size(42.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            // Overlapping Content
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 230.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.background,
            ) {
                when {
                    state.isLoading -> LoadingContent(Modifier.fillMaxSize())
                    state.hasLoadError || state.profile == null -> ErrorContent(
                        modifier = Modifier.fillMaxSize(),
                        messageRes = state.loadErrorMessageRes,
                        onRetry = { onIntent(PersonalDetailsUIIntent.Retry) },
                    )

                    else -> PersonalDetailsContent(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onFirstNameChanged = { onIntent(PersonalDetailsUIIntent.FirstNameChanged(it)) },
                        onLastNameChanged = { onIntent(PersonalDetailsUIIntent.LastNameChanged(it)) },
                        onAddressChanged = { onIntent(PersonalDetailsUIIntent.HomeAddressChanged(it)) },
                        onLocationPickerClick = { onIntent(PersonalDetailsUIIntent.LocationPickerClicked) },
                        onDatePickerClick = { showDatePicker = true },
                        onSaveClick = { onIntent(PersonalDetailsUIIntent.SaveClicked) },
                    )
                }
            }
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
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onLocationPickerClick: () -> Unit,
    onDatePickerClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val profile = requireNotNull(state.profile)
    val notProvided = stringResource(R.string.profile_personal_details_not_provided)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 32.dp,
            end = 24.dp,
            bottom = 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Identity Group
        item {
            GroupCard {
                if (state.isEditing) {
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_customer_id),
                        value = profile.id?.toString().orEmpty().orNotProvided(notProvided),
                        placeholder = notProvided,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_first_name),
                        value = state.draftFirstName,
                        placeholder = notProvided,
                        onValueChange = onFirstNameChanged,
                        isError = !state.isFirstNameValid,
                        errorMessage = stringResource(R.string.profile_personal_details_name_required),
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_last_name),
                        value = state.draftLastName,
                        placeholder = notProvided,
                        onValueChange = onLastNameChanged,
                        isError = !state.isLastNameValid,
                        errorMessage = stringResource(R.string.profile_personal_details_name_required),
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_dob),
                        value = state.draftDob,
                        placeholder = notProvided,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = onDatePickerClick) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = stringResource(R.string.profile_personal_details_select_dob),
                                )
                            }
                        },
                    )
                } else {
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_customer_id),
                        value = profile.id?.toString().orEmpty().orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_first_name),
                        value = profile.firstName.orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        editable = true,
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_last_name),
                        value = profile.lastName.orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        editable = true,
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_dob),
                        value = profile.dob.orEmpty().orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                        editable = true,
                    )
                }
            }
        }

        // Contact Group
        item {
            GroupCard {
                if (state.isEditing) {
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_email),
                        value = profile.email.orNotProvided(notProvided),
                        placeholder = notProvided,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_phone),
                        value = profile.phoneNumber.orNotProvided(notProvided),
                        placeholder = notProvided,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsEditableField(
                        label = stringResource(R.string.profile_personal_details_home_address),
                        value = state.draftHomeAddress,
                        placeholder = notProvided,
                        onValueChange = onAddressChanged,
                        singleLine = false,
                        minLines = 3,
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    )
                    ProfileLocationCard(
                        latitude = state.draftLatitude,
                        longitude = state.draftLongitude,
                        isEditing = true,
                        onChangeLocation = onLocationPickerClick,
                    )
                } else {
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_email),
                        value = profile.email.orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_phone),
                        value = profile.phoneNumber.orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    PersonalDetailsInfoRow(
                        label = stringResource(R.string.profile_personal_details_home_address),
                        value = profile.homeAddress.orEmpty().orNotProvided(notProvided),
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        editable = true,
                    )
                    ProfileLocationCard(
                        latitude = profile.latitude,
                        longitude = profile.longitude,
                        isEditing = false,
                        onChangeLocation = {},
                    )
                }
            }
        }

        item {
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(top = 8.dp),
                enabled = state.isEditing && state.hasChanges && !state.isSaving &&
                        state.isFirstNameValid && state.isLastNameValid,
                shape = RoundedCornerShape(100.dp), // pill shape
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                ),
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = content
        )
    }
}

@Composable
private fun PersonalDetailsInfoRow(
    label: String,
    value: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    editable: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
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
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            placeholder = {
                Text(text = placeholder)
            },
            supportingText = if (isError && errorMessage != null) {
                { Text(text = errorMessage) }
            } else {
                null
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                errorContainerColor = MaterialTheme.colorScheme.surface,
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
                latitude = 30.0444,
                longitude = 31.2357,
            ),
            isLoading = false,
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onNavigateBack = {},
        onIntent = {},
    )
}
