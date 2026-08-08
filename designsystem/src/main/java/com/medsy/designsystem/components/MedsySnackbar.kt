package com.medsy.designsystem.components

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
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
    alignment: Alignment = Alignment.BottomCenter,
) {
    val currentData = hostState.currentSnackbarData
    var lastData by remember { mutableStateOf<SnackbarData?>(null) }
    if (currentData != null) lastData = currentData

    LaunchedEffect(currentData) {
        if (currentData != null) {
            delay(1800L.milliseconds)
            currentData.dismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 80.dp),
        contentAlignment = alignment,
    ) {
        AnimatedVisibility(
            visible = currentData != null,
            enter = slideInVertically(
                initialOffsetY = { if (alignment == Alignment.TopCenter) -it else it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                )
            ) + fadeIn(animationSpec = tween(150)),
            exit = slideOutVertically(
                targetOffsetY = { if (alignment == Alignment.TopCenter) -it else it },
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

    val isError = type == MedsySnackbarType.Error
    
    val backgroundColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isError) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val iconColor = when (type) {
        MedsySnackbarType.Success -> MaterialTheme.colorScheme.primary
        MedsySnackbarType.Error -> MaterialTheme.colorScheme.onError
        MedsySnackbarType.Info -> MaterialTheme.colorScheme.secondary
    }
    val icon = when (type) {
        MedsySnackbarType.Success -> Icons.Filled.CheckCircle
        MedsySnackbarType.Error -> Icons.Filled.Error
        MedsySnackbarType.Info -> Icons.Filled.Info
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        shadowElevation = 8.dp,
        border = if (!isError) androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ) else null,
        modifier = Modifier.wrapContentWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = visuals.message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                )
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

suspend fun SnackbarHostState.showMessage(
    context: Context,
    @StringRes messageRes: Int,
    isSuccess: Boolean,
    actionLabel: String? = null,
): SnackbarResult? {
    val message = ContextCompat.getString(context, messageRes)
    return if (isSuccess) {
        showSuccess(message, actionLabel)
    } else {
        showError(message, actionLabel)
    }
}
