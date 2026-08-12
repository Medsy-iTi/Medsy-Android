package com.medsy.presentation.aichat.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatIntent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatMessageAction
import com.medsy.presentation.R
import com.medsy.presentation.aichat.AiChatState
import com.medsy.presentation.aichat.AiChatUIIntent

@Composable
fun AiChatConversation(
    state: AiChatState,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(
        state.messages.size,
        state.isResponding,
        state.errorMessageRes,
    ) {
        val hasFooter = state.isResponding || state.errorMessageRes != null
        val lastIndex = state.messages.size - 1 + if (hasFooter) 1 else 0
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.messages, key = AiChatMessage::id) { message ->
            AiChatMessageItem(message = message, onIntent = onIntent)
        }
        if (state.isResponding) {
            item(key = "typing") { AiChatTypingIndicator() }
        }
        state.errorMessageRes?.let { messageRes ->
            item(key = "send_error") {
                SendRetryCard(messageRes = messageRes, onIntent = onIntent)
            }
        }
    }
}

@Composable
private fun LazyItemScope.AiChatMessageItem(
    message: AiChatMessage,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    val itemModifier = Modifier.animateItem()
    when (val content = message.content) {
        is AiChatContent.UserText -> UserBubble(content, itemModifier)
        is AiChatContent.AssistantMessage -> AssistantMessageItem(
            content = content,
            onIntent = onIntent,
            modifier = itemModifier,
        )
    }
}

@Composable
private fun UserBubble(content: AiChatContent.UserText, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                )
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            content.imageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = stringResource(R.string.ai_chat_sent_photo_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }
            if (content.value.isNotBlank()) {
                Text(
                    text = content.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun AssistantMessageItem(
    content: AiChatContent.AssistantMessage,
    onIntent: (AiChatUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isReminder = content.intent in REMINDER_INTENTS
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.Top,
        ) {
            AiChatAvatar(size = 28.dp)
            Spacer(Modifier.width(8.dp))
            val answer = content.answer.takeIf(String::isNotBlank)
                ?: stringResource(R.string.ai_chat_catalog_empty_answer)
            if (isReminder) {
                AiChatReminderCard(answer = answer)
            } else {
                AssistantBubble(text = answer)
            }
        }

        when (content.intent) {
            AiChatIntent.EMERGENCY -> if (content.emergencyNumbers.isNotEmpty()) {
                AiChatEmergencyCard(
                    numbers = content.emergencyNumbers,
                    onCall = { onIntent(AiChatUIIntent.EmergencyCallClicked(it)) },
                )
            }

            AiChatIntent.DOCTOR_SPECIALIZATION -> if (content.doctorSpecializations.isNotEmpty()) {
                AiChatSpecializationCard(specializations = content.doctorSpecializations)
            }

            AiChatIntent.CATEGORY_BROWSE -> content.categories.forEach { category ->
                AiChatCategoryCard(
                    category = category,
                    onClick = {
                        onIntent(AiChatUIIntent.CategoryClicked(category.id, category.name))
                    },
                )
            }

            else -> Unit
        }

        when (val action = content.action) {
            is AiChatMessageAction.AddedToCart -> AiChatCartSuccessCard(
                action = action,
                onViewCart = { onIntent(AiChatUIIntent.ViewCartClicked) },
            )

            AiChatMessageAction.CreateRequest -> AiChatConfirmRequestCard(
                onConfirm = { onIntent(AiChatUIIntent.ConfirmRequestClicked) },
            )

            null -> Unit
        }

        content.products.forEach { product ->
            AiChatProductCard(
                product = product,
                onOpenProduct = {
                    onIntent(AiChatUIIntent.ProductClicked(product.productId))
                },
                onAddToCart = {
                    onIntent(AiChatUIIntent.AddToCartClicked(product.productId))
                },
            )
        }

        content.disclaimer?.let { disclaimer ->
            AiChatMessageDisclaimer(text = disclaimer)
        }
    }
}

@Composable
private fun AssistantBubble(text: String) {
    ChatMarkdownText(
        text = text,
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
private fun SendRetryCard(
    @StringRes messageRes: Int,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.warningContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.ai_chat_catalog_error_title),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.onWarningContainer,
            )
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.onWarningContainer,
            )
            OutlinedButton(
                onClick = { onIntent(AiChatUIIntent.RetrySend) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ai_chat_catalog_retry))
            }
        }
    }
}

private val REMINDER_INTENTS = setOf(
    AiChatIntent.SET_REMINDER,
    AiChatIntent.DELETE_REMINDER,
    AiChatIntent.LIST_REMINDERS,
)
