package com.medsy.presentation.prescription.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.domain.prescription.model.ExtractedMedicine
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
fun QuantityControl(
    item: ExtractedMedicine,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        QuantityButton(
            icon = Icons.Filled.Remove,
            primary = false,
            description = stringResource(R.string.prescription_decrease_quantity),
            onClick = {
                onIntent(PrescriptionUIIntent.DecreaseQuantityClicked(item.localItemId))
            },
        )
        Text(
            text = item.quantity.toString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.size(width = 28.dp, height = 20.dp),
        )
        QuantityButton(
            icon = Icons.Filled.Add,
            primary = true,
            description = stringResource(R.string.prescription_increase_quantity),
            onClick = {
                onIntent(PrescriptionUIIntent.IncreaseQuantityClicked(item.localItemId))
            },
        )
    }
}

