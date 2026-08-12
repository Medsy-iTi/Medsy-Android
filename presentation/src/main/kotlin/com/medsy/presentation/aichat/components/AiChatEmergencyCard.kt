package com.medsy.presentation.aichat.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.aichat.model.AiEmergencyNumber
import com.medsy.domain.aichat.model.AiEmergencyService
import com.medsy.presentation.R

@Composable
fun AiChatEmergencyCard(
    numbers: List<AiEmergencyNumber>,
    onCall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Emergency,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_chat_emergency_numbers_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            numbers.forEach { emergency ->
                EmergencyServiceRow(emergency = emergency, onCall = onCall)
            }
        }
    }
}

@Composable
private fun EmergencyServiceRow(
    emergency: AiEmergencyNumber,
    onCall: (String) -> Unit,
) {
    val style = emergency.service.style()
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(style.container, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = null,
                    tint = style.content,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(style.labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = emergency.number,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Button(
                onClick = { onCall(emergency.number) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
                modifier = Modifier.height(46.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.ai_chat_emergency_call),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private data class EmergencyServiceStyle(
    val icon: ImageVector,
    @param:StringRes val labelRes: Int,
    val container: Color,
    val content: Color,
)

@Composable
private fun AiEmergencyService.style(): EmergencyServiceStyle = when (this) {
    AiEmergencyService.AMBULANCE -> EmergencyServiceStyle(
        icon = Icons.Filled.Emergency,
        labelRes = R.string.ai_chat_emergency_ambulance,
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.error,
    )

    AiEmergencyService.POLICE -> EmergencyServiceStyle(
        icon = Icons.Filled.LocalPolice,
        labelRes = R.string.ai_chat_emergency_police,
        container = MaterialTheme.extendedColors.blueContainer,
        content = MaterialTheme.extendedColors.blueContent,
    )

    AiEmergencyService.FIRE -> EmergencyServiceStyle(
        icon = Icons.Filled.LocalFireDepartment,
        labelRes = R.string.ai_chat_emergency_fire,
        container = MaterialTheme.extendedColors.orangeContainer,
        content = MaterialTheme.extendedColors.orangeContent,
    )
}
