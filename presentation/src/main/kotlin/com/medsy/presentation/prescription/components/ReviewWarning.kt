package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R

@Composable
fun ReviewWarning() {
    val colors = MaterialTheme.extendedColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.prescriptionWarningContainer, RoundedCornerShape(16.dp))
            .border(
                1.dp,
                colors.prescriptionWarningBorder.copy(alpha = 0.7f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = colors.prescriptionWarningContent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(R.string.prescription_confirm_before_add),
            color = colors.prescriptionWarningContent,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
    }
}
