package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors

@Composable
fun TipRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    MaterialTheme.extendedColors.prescriptionSuccessSoft,
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.prescriptionPrimary,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = text,
            color = MaterialTheme.extendedColors.prescriptionSupporting,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

