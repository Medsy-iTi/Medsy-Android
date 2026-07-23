package com.medsy.presentation.prescription.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors

@Composable
fun LoadingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "prescriptionLoading")
    val opacity by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "prescriptionLoadingOpacity",
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .alpha((opacity - index * 0.12f).coerceIn(0.25f, 1f))
                    .background(MaterialTheme.extendedColors.prescriptionPrimary, CircleShape),
            )
        }
    }
}
