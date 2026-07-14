package com.medsy.presentation.auth.register.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SectionTitle(
    textRes: Int,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF1E7C3F),
) {
    Text(
        text = stringResource(textRes),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier,
    )
}
