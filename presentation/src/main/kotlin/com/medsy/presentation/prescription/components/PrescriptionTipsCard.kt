package com.medsy.presentation.prescription.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R

@Composable
fun PrescriptionTipsCard(showThreeTips: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(
                if (showThreeTips) R.string.prescription_best_result else R.string.prescription_quick_reminder,
            ),
            color = if (showThreeTips) {
                MaterialTheme.extendedColors.prescriptionTitle
            } else {
                MaterialTheme.extendedColors.prescriptionSupporting
            },
            fontWeight = FontWeight.Bold,
        )
        TipRow(
            Icons.Filled.CenterFocusStrong,
            stringResource(R.string.prescription_tip_clear_names)
        )
        TipRow(Icons.Filled.CameraAlt, stringResource(R.string.prescription_tip_avoid_shadows))
        if (showThreeTips) {
            TipRow(Icons.Filled.CropFree, stringResource(R.string.prescription_tip_full_image))
        }
    }
}

