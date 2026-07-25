package com.medsy.presentation.splash.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path

@Composable
fun SplashWaves(modifier: Modifier = Modifier) {
    val waveColor1 = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val waveColor2 = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val path1 = Path().apply {
            moveTo(0f, height * 0.4f)
            quadraticTo(
                width * 0.2f, height * 0.2f,
                width * 0.5f, height * 0.4f
            )
            quadraticTo(
                width * 0.8f, height * 0.6f,
                width, height * 0.4f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(path1, color = waveColor2)

        val path2 = Path().apply {
            moveTo(0f, height * 0.6f)
            quadraticTo(
                width * 0.3f, height * 0.7f,
                width * 0.6f, height * 0.5f
            )
            quadraticTo(
                width * 0.85f, height * 0.35f,
                width, height * 0.6f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(path2, color = waveColor1)
    }
}
