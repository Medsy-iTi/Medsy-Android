package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors

@Composable
fun SmallActionButton(
    text: String,
    primary: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    Text(
        text = text,
        color = if (primary) MaterialTheme.colorScheme.onPrimary else colors.prescriptionTitle,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(
                if (primary) {
                    colors.prescriptionPrimary
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                },
                RoundedCornerShape(14.dp),
            )
            .border(
                1.dp,
                if (primary) colors.prescriptionPrimary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
    )
}

