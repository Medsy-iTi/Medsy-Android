package com.medsy.presentation.prescription.prescriptionreview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.prescription.model.ExtractedMedicine
import com.medsy.presentation.R

@Composable
internal fun MedicineIdentityRow(item: ExtractedMedicine) {
    val colors = MaterialTheme.extendedColors
    val suggestion = item.selectedMedicine

    val medicineName = suggestion?.name ?: item.extractedName ?: item.rawText
    val packDescription = suggestion?.strength ?: item.extractedStrength ?: ""
    val price = suggestion?.price ?: 0
    val isWarning = !item.isConfirmed

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isWarning) colors.prescriptionWarningContainer else colors.prescriptionSuccessSoft,
                    RoundedCornerShape(14.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Medication,
                contentDescription = null,
                tint = if (isWarning) colors.prescriptionWarningContent else colors.prescriptionPrimary,
                modifier = Modifier.size(30.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = medicineName,
                color = colors.prescriptionTitle,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = packDescription,
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.prescription_price_egp, price),
                color = colors.prescriptionPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.prescription_per_pack),
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}