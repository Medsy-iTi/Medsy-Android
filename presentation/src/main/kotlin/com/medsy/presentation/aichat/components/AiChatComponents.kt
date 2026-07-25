package com.medsy.presentation.aichat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatImageKind
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatScenario
import com.medsy.domain.aichat.model.AiMedicine
import com.medsy.domain.aichat.model.AiPharmacy
import com.medsy.presentation.R
import com.medsy.presentation.aichat.AiChatState
import com.medsy.presentation.aichat.AiChatUIIntent

@Composable
fun AiChatTopBar(
    hasMessages: Boolean,
    onBack: () -> Unit,
    onNewChat: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_back),
                )
            }
            AiAvatar(size = 36)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.ai_chat_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.extendedColors.success)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.ai_chat_online),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onNewChat, enabled = hasMessages) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.ai_chat_new_chat_description),
                    tint = if (hasMessages) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun AiAvatar(size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size((size / 2).dp),
        )
    }
}

@Composable
fun AiChatWelcome(
    onIntent: (AiChatUIIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { AiDisclaimer() }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AiAvatar(size = 64)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.ai_chat_greeting),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.ai_chat_welcome_supporting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                )
            }
        }
        item {
            QuickActionRow(
                first = QuickAction(
                    R.string.ai_chat_prompt_headache,
                    Icons.Outlined.HealthAndSafety,
                    AiChatScenario.HEADACHE_TRIAGE,
                ),
                second = QuickAction(
                    R.string.ai_chat_prompt_pharmacies,
                    Icons.Outlined.LocalPharmacy,
                    AiChatScenario.NEARBY_PHARMACIES,
                ),
                onIntent = onIntent,
            )
        }
        item {
            QuickActionRow(
                first = QuickAction(
                    R.string.ai_chat_prompt_prescription,
                    Icons.Outlined.AddPhotoAlternate,
                    AiChatScenario.PRESCRIPTION_IMAGE,
                ),
                second = QuickAction(
                    R.string.ai_chat_prompt_interaction,
                    Icons.Outlined.SyncAlt,
                    AiChatScenario.INTERACTION_CHECK,
                ),
                onIntent = onIntent,
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                WelcomeChip(R.string.ai_chat_prompt_medicine_info, AiChatScenario.MEDICINE_INFORMATION, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_track_order, AiChatScenario.ORDER_TRACKING, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_reorder, AiChatScenario.REORDER_MEDICINES, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_reminder, AiChatScenario.DOSE_REMINDER, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_cheaper, AiChatScenario.CHEAPER_EQUIVALENT, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_medicine_photo, AiChatScenario.MEDICINE_IMAGE, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_emergency_demo, AiChatScenario.EMERGENCY, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_unreadable_demo, AiChatScenario.UNREADABLE_IMAGE, onIntent)
                WelcomeChip(R.string.ai_chat_prompt_no_medicine_demo, AiChatScenario.NO_MEDICINE_FOUND, onIntent)
            }
        }
    }
}

private data class QuickAction(
    val label: Int,
    val icon: ImageVector,
    val scenario: AiChatScenario,
)

@Composable
private fun QuickActionRow(
    first: QuickAction,
    second: QuickAction,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionCard(first, Modifier.weight(1f), onIntent)
        QuickActionCard(second, Modifier.weight(1f), onIntent)
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(
        modifier = modifier.clickable {
            onIntent(AiChatUIIntent.QuickActionClicked(action.scenario))
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(action.label),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
            )
        }
    }
}

@Composable
private fun WelcomeChip(
    label: Int,
    scenario: AiChatScenario,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    AssistChip(
        onClick = { onIntent(AiChatUIIntent.QuickActionClicked(scenario)) },
        label = { Text(stringResource(label)) },
    )
}

@Composable
private fun AiDisclaimer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.extendedColors.infoContainer,
                RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.onInfoContainer,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.ai_chat_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extendedColors.onInfoContainer,
        )
    }
}

@Composable
fun AiChatConversation(
    state: AiChatState,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size, state.isResponding) {
        val lastIndex = state.messages.size + if (state.isResponding) 1 else 0
        if (lastIndex > 0) listState.animateScrollToItem(lastIndex)
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "disclaimer") { AiDisclaimer() }
        items(state.messages, key = AiChatMessage::id) { message ->
            AiChatMessageItem(message = message, state = state, onIntent = onIntent)
        }
        if (state.isResponding) {
            item(key = "typing") { TypingIndicator() }
        }
    }
}

@Composable
private fun AiChatMessageItem(
    message: AiChatMessage,
    state: AiChatState,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    when (val content = message.content) {
        is AiChatContent.UserScenario -> UserBubble(scenarioPrompt(content.scenario))
        is AiChatContent.UserText -> UserBubble(content.value)
        is AiChatContent.AssistantResponse -> AssistantResponse(
            response = content,
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                )
                .padding(horizontal = 14.dp, vertical = 11.dp),
        )
    }
}

@Composable
private fun AssistantResponse(
    response: AiChatContent.AssistantResponse,
    state: AiChatState,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        AiAvatar(size = 28)
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth(0.94f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AssistantBubble(assistantText(response.scenario))
            when (response.scenario) {
                AiChatScenario.HEADACHE_TRIAGE -> {
                    SpecialtyCard()
                    response.medicines.firstOrNull()?.let {
                        MedicineCard(it, onIntent)
                    }
                }
                AiChatScenario.NEARBY_PHARMACIES -> {
                    response.pharmacies.forEach { PharmacyCard(it, onIntent) }
                    AiCallCard(state.isAiCallInProgress, onIntent)
                }
                AiChatScenario.PRESCRIPTION_IMAGE ->
                    PrescriptionCard(response.medicines, onIntent)
                AiChatScenario.MEDICINE_IMAGE,
                AiChatScenario.MEDICINE_INFORMATION -> response.medicines.firstOrNull()?.let {
                    MedicineInformationCard(it, onIntent)
                }
                AiChatScenario.INTERACTION_CHECK -> InteractionCard()
                AiChatScenario.DOSE_REMINDER -> ReminderCard(state.isReminderConfirmed, onIntent)
                AiChatScenario.REORDER_MEDICINES -> ReorderCard(response.medicines, onIntent)
                AiChatScenario.ORDER_TRACKING -> OrderTrackingCard()
                AiChatScenario.CHEAPER_EQUIVALENT -> AlternativeCard(response.medicines, onIntent)
                AiChatScenario.EMERGENCY -> EmergencyCard(onIntent)
                AiChatScenario.UNREADABLE_IMAGE,
                AiChatScenario.NO_MEDICINE_FOUND -> RetryCard(onIntent)
                AiChatScenario.UNSUPPORTED -> UnsupportedCard()
            }
        }
    }
}

@Composable
private fun AssistantBubble(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
                RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
            )
            .padding(horizontal = 14.dp, vertical = 11.dp),
    )
}

@Composable
private fun SpecialtyCard() {
    InfoCard(
        icon = Icons.Outlined.LocalHospital,
        title = stringResource(R.string.ai_chat_specialty_title),
        body = stringResource(R.string.ai_chat_specialty_body),
    )
}

@Composable
private fun MedicineCard(
    medicine: AiMedicine,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = stringResource(R.string.ai_chat_otc_badge),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            MedicineSummary(medicine)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { onIntent(AiChatUIIntent.AddToCartClicked(medicine.productId)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.AddShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ai_chat_add_to_cart))
            }
            Text(
                text = stringResource(R.string.ai_chat_medicine_confirmation),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun MedicineSummary(medicine: AiMedicine) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(medicine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                medicine.activeIngredient,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(R.string.ai_chat_price_egp, medicine.priceEgp),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PharmacyCard(
    pharmacy: AiPharmacy,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocalPharmacy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(pharmacy.name, fontWeight = FontWeight.SemiBold)
                    Text(
                        stringResource(R.string.ai_chat_pharmacy_distance, pharmacy.distanceKm),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (pharmacy.isOpen) {
                    Text(
                        stringResource(R.string.ai_chat_open),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.extendedColors.success,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = { onIntent(AiChatUIIntent.CallPharmacyClicked(pharmacy.phoneNumber)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Call, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.ai_chat_call))
                }
                OutlinedButton(
                    onClick = { onIntent(AiChatUIIntent.DirectionsClicked(pharmacy)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Directions, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.ai_chat_directions))
                }
            }
        }
    }
}

@Composable
private fun AiCallCard(
    inProgress: Boolean,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    InfoCard(
        icon = Icons.Outlined.Call,
        title = stringResource(if (inProgress) R.string.ai_chat_ai_calling else R.string.ai_chat_ai_call_title),
        body = stringResource(if (inProgress) R.string.ai_chat_ai_call_body_active else R.string.ai_chat_ai_call_body),
        action = {
            TextButton(
                onClick = {
                    onIntent(
                        if (inProgress) AiChatUIIntent.EndAiCallClicked else AiChatUIIntent.StartAiCallClicked
                    )
                }
            ) {
                Text(stringResource(if (inProgress) R.string.ai_chat_end_call else R.string.ai_chat_call_for_me))
            }
        },
    )
}

@Composable
private fun PrescriptionCard(
    medicines: List<AiMedicine>,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.ai_chat_detected_medicines), fontWeight = FontWeight.Bold)
            medicines.forEachIndexed { index, medicine ->
                if (index > 0) HorizontalDivider()
                MedicineSummary(medicine)
                medicine.confidencePercent?.let {
                    Text(
                        stringResource(R.string.ai_chat_confidence, it),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.extendedColors.success,
                    )
                }
            }
            Button(
                onClick = {
                    onIntent(AiChatUIIntent.ReorderClicked(medicines.map(AiMedicine::productId)))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.ai_chat_add_all_to_cart))
            }
            Text(
                stringResource(R.string.ai_chat_prescription_review_notice),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MedicineInformationCard(
    medicine: AiMedicine,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MedicineSummary(medicine)
            Text(stringResource(R.string.ai_chat_medicine_used_for), fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.ai_chat_medicine_used_for_body), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.ai_chat_how_to_take), fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.ai_chat_how_to_take_body), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.ai_chat_side_effects), fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.ai_chat_side_effects_body), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(
                onClick = { onIntent(AiChatUIIntent.AddToCartClicked(medicine.productId)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.ai_chat_add_to_cart))
            }
        }
    }
}

@Composable
private fun InteractionCard() {
    WarningCard(
        title = stringResource(R.string.ai_chat_interaction_title),
        body = stringResource(R.string.ai_chat_interaction_body),
    )
}

@Composable
private fun ReminderCard(
    confirmed: Boolean,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    if (confirmed) {
        InfoCard(
            icon = Icons.Outlined.Alarm,
            title = stringResource(R.string.ai_chat_reminder_set),
            body = stringResource(R.string.ai_chat_reminder_body),
        )
    } else {
        InfoCard(
            icon = Icons.Outlined.Alarm,
            title = stringResource(R.string.ai_chat_reminder_title),
            body = stringResource(R.string.ai_chat_reminder_body),
            action = {
                TextButton(onClick = { onIntent(AiChatUIIntent.SetReminderClicked) }) {
                    Text(stringResource(R.string.ai_chat_set_reminder))
                }
            },
        )
    }
}

@Composable
private fun ReorderCard(
    medicines: List<AiMedicine>,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            medicines.forEach { MedicineSummary(it) }
            Button(
                onClick = { onIntent(AiChatUIIntent.ReorderClicked(medicines.map(AiMedicine::productId))) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.ai_chat_reorder_action))
            }
        }
    }
}

@Composable
private fun OrderTrackingCard() {
    InfoCard(
        icon = Icons.Outlined.Refresh,
        title = stringResource(R.string.ai_chat_order_tracking_title),
        body = stringResource(R.string.ai_chat_order_tracking_body),
    )
}

@Composable
private fun AlternativeCard(
    medicines: List<AiMedicine>,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    val alternative = medicines.lastOrNull() ?: return
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.ai_chat_same_ingredient), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            MedicineSummary(alternative)
            Text(stringResource(R.string.ai_chat_savings, 122), color = MaterialTheme.extendedColors.success, fontWeight = FontWeight.Bold)
            Button(
                onClick = { onIntent(AiChatUIIntent.AddToCartClicked(alternative.productId)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.ai_chat_choose_alternative))
            }
        }
    }
}

@Composable
private fun EmergencyCard(onIntent: (AiChatUIIntent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.ai_chat_emergency_title),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(stringResource(R.string.ai_chat_emergency_body), color = MaterialTheme.colorScheme.onErrorContainer)
            Button(
                onClick = { onIntent(AiChatUIIntent.CallAmbulanceClicked) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.Call, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ai_chat_call_ambulance))
            }
            OutlinedButton(
                onClick = { onIntent(AiChatUIIntent.FindNearestHospitalClicked) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.LocalHospital, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ai_chat_nearest_hospital))
            }
            Text(
                stringResource(R.string.ai_chat_emergency_waiting),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
private fun RetryCard(onIntent: (AiChatUIIntent) -> Unit) {
    OutlinedButton(
        onClick = { onIntent(AiChatUIIntent.AttachmentClicked) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Outlined.Refresh, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.ai_chat_try_another_image))
    }
}

@Composable
private fun UnsupportedCard() {
    Text(
        stringResource(R.string.ai_chat_unsupported_actions),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    body: String,
    action: (@Composable () -> Unit)? = null,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.extendedColors.infoContainer)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.extendedColors.onInfoContainer)
                Spacer(Modifier.width(8.dp))
                Text(title, color = MaterialTheme.extendedColors.onInfoContainer, fontWeight = FontWeight.Bold)
            }
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.onInfoContainer,
                modifier = Modifier.padding(top = 8.dp),
            )
            action?.invoke()
        }
    }
}

@Composable
private fun WarningCard(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.extendedColors.warningContainer)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = MaterialTheme.extendedColors.onWarningContainer)
                Spacer(Modifier.width(8.dp))
                Text(title, color = MaterialTheme.extendedColors.onWarningContainer, fontWeight = FontWeight.Bold)
            }
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.extendedColors.onWarningContainer, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AiAvatar(size = 28)
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.ai_chat_typing),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

@Composable
fun AiChatComposer(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onAttachment: () -> Unit,
    onVoice: () -> Unit,
    onSend: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // union so the composer clears the gesture bar, and rides the
                // keyboard instead of stacking both insets when the IME is up.
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onAttachment, enabled = enabled) {
                Icon(
                    Icons.Outlined.AddPhotoAlternate,
                    contentDescription = stringResource(R.string.ai_chat_attach_description),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            TextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.ai_chat_input_hint), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.surface,
                ),
                maxLines = 4,
            )
            if (value.isBlank()) {
                IconButton(onClick = onVoice, enabled = enabled) {
                    Icon(Icons.Outlined.Mic, contentDescription = stringResource(R.string.ai_chat_voice_description))
                }
            } else {
                FilledIconButton(onClick = onSend, enabled = enabled) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = stringResource(R.string.ai_chat_send_description))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatAttachmentSheet(
    onDismiss: () -> Unit,
    onCamera: (AiChatImageKind) -> Unit,
    onGallery: (AiChatImageKind) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.ai_chat_add_photo_title), style = MaterialTheme.typography.titleLarge)
            AttachmentOption(Icons.Outlined.PhotoCamera, R.string.ai_chat_take_prescription, R.string.ai_chat_take_prescription_body) {
                onCamera(AiChatImageKind.PRESCRIPTION)
            }
            AttachmentOption(Icons.Outlined.Image, R.string.ai_chat_choose_prescription, R.string.ai_chat_choose_prescription_body) {
                onGallery(AiChatImageKind.PRESCRIPTION)
            }
            AttachmentOption(Icons.Outlined.CameraAlt, R.string.ai_chat_take_medicine, R.string.ai_chat_take_medicine_body) {
                onCamera(AiChatImageKind.MEDICINE)
            }
            AttachmentOption(Icons.Outlined.Image, R.string.ai_chat_choose_medicine, R.string.ai_chat_choose_medicine_body) {
                onGallery(AiChatImageKind.MEDICINE)
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: ImageVector,
    title: Int,
    body: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(stringResource(title), fontWeight = FontWeight.SemiBold)
            Text(stringResource(body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AiChatNewChatDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null) },
        title = { Text(stringResource(R.string.ai_chat_new_chat_title)) },
        text = { Text(stringResource(R.string.ai_chat_new_chat_body)) },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.ai_chat_cancel)) } },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.ai_chat_start_new)) } },
    )
}

@Composable
private fun scenarioPrompt(scenario: AiChatScenario): String = stringResource(
    when (scenario) {
        AiChatScenario.HEADACHE_TRIAGE -> R.string.ai_chat_prompt_headache
        AiChatScenario.NEARBY_PHARMACIES -> R.string.ai_chat_prompt_pharmacies
        AiChatScenario.PRESCRIPTION_IMAGE -> R.string.ai_chat_prompt_prescription
        AiChatScenario.MEDICINE_IMAGE -> R.string.ai_chat_prompt_medicine_photo
        AiChatScenario.MEDICINE_INFORMATION -> R.string.ai_chat_prompt_medicine_info
        AiChatScenario.INTERACTION_CHECK -> R.string.ai_chat_prompt_interaction
        AiChatScenario.DOSE_REMINDER -> R.string.ai_chat_prompt_reminder
        AiChatScenario.REORDER_MEDICINES -> R.string.ai_chat_prompt_reorder
        AiChatScenario.ORDER_TRACKING -> R.string.ai_chat_prompt_track_order
        AiChatScenario.CHEAPER_EQUIVALENT -> R.string.ai_chat_prompt_cheaper
        AiChatScenario.EMERGENCY -> R.string.ai_chat_emergency_user_message
        AiChatScenario.UNREADABLE_IMAGE -> R.string.ai_chat_prompt_unreadable_demo
        AiChatScenario.NO_MEDICINE_FOUND -> R.string.ai_chat_prompt_no_medicine_demo
        AiChatScenario.UNSUPPORTED -> R.string.ai_chat_prompt_unsupported
    }
)

@Composable
private fun assistantText(scenario: AiChatScenario): String = stringResource(
    when (scenario) {
        AiChatScenario.HEADACHE_TRIAGE -> R.string.ai_chat_response_headache
        AiChatScenario.NEARBY_PHARMACIES -> R.string.ai_chat_response_pharmacies
        AiChatScenario.PRESCRIPTION_IMAGE -> R.string.ai_chat_response_prescription
        AiChatScenario.MEDICINE_IMAGE -> R.string.ai_chat_response_medicine_image
        AiChatScenario.MEDICINE_INFORMATION -> R.string.ai_chat_response_medicine_info
        AiChatScenario.INTERACTION_CHECK -> R.string.ai_chat_response_interaction
        AiChatScenario.DOSE_REMINDER -> R.string.ai_chat_response_reminder
        AiChatScenario.REORDER_MEDICINES -> R.string.ai_chat_response_reorder
        AiChatScenario.ORDER_TRACKING -> R.string.ai_chat_response_track_order
        AiChatScenario.CHEAPER_EQUIVALENT -> R.string.ai_chat_response_cheaper
        AiChatScenario.EMERGENCY -> R.string.ai_chat_response_emergency
        AiChatScenario.UNREADABLE_IMAGE -> R.string.ai_chat_response_unreadable
        AiChatScenario.NO_MEDICINE_FOUND -> R.string.ai_chat_response_no_medicine
        AiChatScenario.UNSUPPORTED -> R.string.ai_chat_response_unsupported
    }
)
