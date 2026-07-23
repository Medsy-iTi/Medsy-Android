package com.medsy.presentation.prescription.medicinepicker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.prescription.model.Medicine
import com.medsy.presentation.R

@Composable
fun MedicinePickerCard(medicine: Medicine, onClick: () -> Unit) {
    val colors = MaterialTheme.extendedColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = medicine.imageUrl,
            contentDescription = medicine.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.prescriptionSuccessSoft),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
            error = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = medicine.name,
                color = colors.prescriptionTitle,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = medicine.strength ?: "",
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.prescription_price_egp, medicine.price),
                color = colors.prescriptionPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.prescription_select_medicine),
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
