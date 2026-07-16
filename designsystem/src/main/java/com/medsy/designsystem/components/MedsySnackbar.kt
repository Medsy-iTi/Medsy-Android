package com.medsy.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


enum class MedsySnackbarType { Success, Error, Info }


private class MedsySnackbarVisuals(
    override val message: String,
    val type: MedsySnackbarType,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

@Composable
fun MedsySnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val currentData = hostState.currentSnackbarData
    var lastData by remember { mutableStateOf<SnackbarData?>(null) }
    if (currentData != null) lastData = currentData

    LaunchedEffect(currentData) {
        if (currentData != null) {
            delay(2500L.milliseconds)
            currentData.dismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visible = currentData != null,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                )
            ) + fadeIn(animationSpec = tween(150)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(220),
            ) + fadeOut(animationSpec = tween(180)),
        ) {
            val data = lastData ?: return@AnimatedVisibility
            MedsySnackbarBanner(data = data)
        }
    }
}


@Composable
private fun MedsySnackbarBanner(data: SnackbarData) {
    val visuals = data.visuals
    val type = (visuals as? MedsySnackbarVisuals)?.type ?: MedsySnackbarType.Error

    val baseColor = when (type) {
        MedsySnackbarType.Success -> MaterialTheme.extendedColors.success
        MedsySnackbarType.Error   -> MaterialTheme.colorScheme.error
        MedsySnackbarType.Info    -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val onBaseColor = when (type) {
        MedsySnackbarType.Success -> MaterialTheme.extendedColors.onSuccess
        MedsySnackbarType.Error   -> MaterialTheme.colorScheme.onError
        MedsySnackbarType.Info    -> MaterialTheme.colorScheme.surface
    }
    val icon = when (type) {
        MedsySnackbarType.Success -> Icons.Filled.CheckCircle
        MedsySnackbarType.Error   -> Icons.Filled.Error
        MedsySnackbarType.Info    -> Icons.Filled.Info
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = baseColor.copy(alpha = 0.35f),
        ),
        shadowElevation = 12.dp,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(baseColor.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = baseColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = visuals.message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

suspend fun SnackbarHostState.showError(
    message: String,
    actionLabel: String? = null,
): SnackbarResult? = showUnique(message, MedsySnackbarType.Error, actionLabel)

suspend fun SnackbarHostState.showSuccess(
    message: String,
    actionLabel: String? = null,
): SnackbarResult? = showUnique(message, MedsySnackbarType.Success, actionLabel)

suspend fun SnackbarHostState.showInfo(
    message: String,
    actionLabel: String? = null,
): SnackbarResult? = showUnique(message, MedsySnackbarType.Info, actionLabel)

private suspend fun SnackbarHostState.showUnique(
    message: String,
    type: MedsySnackbarType,
    actionLabel: String?,
): SnackbarResult? {
    val current = currentSnackbarData?.visuals as? MedsySnackbarVisuals
    if (current != null && current.message == message && current.type == type) return null
    currentSnackbarData?.dismiss()
    return showSnackbar(
        MedsySnackbarVisuals(
            message = message,
            type = type,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short,
        )
    )
}
