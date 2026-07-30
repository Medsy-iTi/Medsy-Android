package com.medsy.presentation.aichat.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.aichat.AiChatUIIntent

@Composable
fun AiChatWelcome(
    onIntent: (AiChatUIIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { AiChatDisclaimer() }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AiChatAvatar(size = 64.dp)
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
                    label = R.string.ai_chat_catalog_prompt_info,
                    icon = Icons.Outlined.Medication,
                ),
                second = QuickAction(
                    label = R.string.ai_chat_catalog_prompt_paracetamol,
                    icon = Icons.Outlined.HealthAndSafety,
                ),
                onIntent = onIntent,
            )
        }
        item {
            QuickActionRow(
                first = QuickAction(
                    label = R.string.ai_chat_catalog_prompt_cough,
                    icon = Icons.Outlined.LocalPharmacy,
                ),
                second = QuickAction(
                    label = R.string.ai_chat_catalog_prompt_cheaper,
                    icon = Icons.Outlined.SyncAlt,
                ),
                onIntent = onIntent,
            )
        }
    }
}

private data class QuickAction(
    @StringRes val label: Int,
    val icon: ImageVector,
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
    val question = stringResource(action.label)
    Card(
        modifier = modifier.clickable {
            onIntent(AiChatUIIntent.QuickActionClicked(question))
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
                text = question,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
            )
        }
    }
}
