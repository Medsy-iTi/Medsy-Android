package com.medsy.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import kotlinx.coroutines.delay

enum class MedsySnackbarType { Error, Success }

data class MedsySnackbarData(
    val message: String? = null,
    val messageRes: Int? = null,
    val type: MedsySnackbarType = MedsySnackbarType.Error,
    val id: Long = System.currentTimeMillis()
) {
    init {
        require(message != null || messageRes != null)
    }
}

private const val SNACKBAR_DURATION_MS = 3500L
private const val ANIM_DURATION_MS = 350

@Composable
fun MedsySnackbarHost(
    snackbarData: MedsySnackbarData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf<MedsySnackbarData?>(null) }

    LaunchedEffect(snackbarData) {
        if (snackbarData != null) {
            current = snackbarData
            visible = true
            delay(SNACKBAR_DURATION_MS)
            visible = false
            delay(ANIM_DURATION_MS.toLong())
            onDismiss()
        } else {
            visible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it - 100 },
                animationSpec = tween(ANIM_DURATION_MS)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it - 100 },
                animationSpec = tween(ANIM_DURATION_MS)
            )
        ) {
            current?.let { data ->
                MedsySnackbarBanner(data = data)
            }
        }
    }
}

@Composable
private fun MedsySnackbarBanner(data: MedsySnackbarData) {
    val (bgColor, iconColor, icon) = when (data.type) {
        MedsySnackbarType.Error -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.onError,
            Icons.Filled.Warning
        )
        MedsySnackbarType.Success -> Triple(
            MaterialTheme.extendedColors.success.copy(alpha = 0.95f),
            MaterialTheme.extendedColors.onSuccess,
            Icons.Filled.CheckCircle
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        val text = data.messageRes?.let { stringResource(id = it) } ?: data.message ?: ""
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = iconColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
