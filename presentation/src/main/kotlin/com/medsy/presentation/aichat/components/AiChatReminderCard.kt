package com.medsy.presentation.aichat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import androidx.compose.material3.Icon

/**
 * Replaces the plain bubble for reminder intents: the answer text (already
 * exact — times, duration) rendered inside an alarm-accented surface.
 */
@Composable
fun AiChatReminderCard(
    answer: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
        color = MaterialTheme.extendedColors.infoContainer,
        contentColor = MaterialTheme.extendedColors.onInfoContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Outlined.Alarm,
                contentDescription = stringResource(R.string.ai_chat_reminder_icon_description),
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            ChatMarkdownText(
                text = answer,
                color = MaterialTheme.extendedColors.onInfoContainer,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
