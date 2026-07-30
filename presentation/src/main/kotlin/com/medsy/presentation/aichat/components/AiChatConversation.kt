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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatMessage
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
        val lastIndex = state.messages.size + if (hasFooter) 1 else 0
        if (lastIndex > 0) listState.animateScrollToItem(lastIndex)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "disclaimer") { AiChatDisclaimer() }
        items(state.messages, key = AiChatMessage::id) { message ->
            AiChatMessageItem(message = message, onIntent = onIntent)
        }
        if (state.isResponding) {
            item(key = "typing") { TypingIndicator() }
        }
        state.errorMessageRes?.let { messageRes ->
            item(key = "catalog_error") {
                CatalogRetryCard(messageRes = messageRes, onIntent = onIntent)
            }
        }
    }
}

@Composable
private fun AiChatMessageItem(
    message: AiChatMessage,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    when (val content = message.content) {
        is AiChatContent.UserText -> UserBubble(content.value)
        is AiChatContent.AssistantResponse -> AssistantResponse(
            response = content,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
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
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            AiChatAvatar(size = 28.dp)
            Spacer(Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AssistantBubble(
                    text = response.answer.takeIf(String::isNotBlank)
                        ?: stringResource(R.string.ai_chat_catalog_empty_answer)
                )
                if (response.products.isEmpty()) {
                    Text(
                        text = stringResource(R.string.ai_chat_catalog_no_products),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.ai_chat_catalog_products_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        response.products.forEach { product ->
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
private fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AiChatAvatar(size = 28.dp)
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.ai_chat_typing),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    RoundedCornerShape(16.dp),
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun CatalogRetryCard(
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
                onClick = { onIntent(AiChatUIIntent.RetryCatalogQuestion) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ai_chat_catalog_retry))
            }
        }
    }
}
