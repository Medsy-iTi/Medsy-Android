package com.medsy.presentation.prescription.prescriptionreview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors

@Composable
internal fun SummaryCountChip(
    count: Int,
    label: String,
    warning: Boolean,
    modifier: Modifier,
) {
    val colors = MaterialTheme.extendedColors
    Column(
        modifier = modifier
            .background(
                if (warning) colors.prescriptionWarningContainer else colors.prescriptionSuccessContainer,
                RoundedCornerShape(16.dp),
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count.toString(),
            color = if (warning) colors.prescriptionWarningContent else colors.prescriptionSuccessContent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = if (warning) colors.prescriptionWarningContent else colors.prescriptionSuccessContent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}