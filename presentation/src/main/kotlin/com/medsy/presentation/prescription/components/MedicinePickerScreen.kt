package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.prescription.model.Medicine
import com.medsy.presentation.R
import com.medsy.presentation.prescription.MedicinePickerMode
import com.medsy.presentation.prescription.MedicinePickerState
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
fun MedicinePickerScreen(
    state: MedicinePickerState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PrescriptionAppBar(
            title = stringResource(
                if (state.mode == MedicinePickerMode.REPLACE) {
                    R.string.prescription_replace_medicine_title
                } else {
                    R.string.prescription_add_medicine_title
                },
            ),
            onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = { onIntent(PrescriptionUIIntent.MedicineQueryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.prescription_search_medicine_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )
        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.extendedColors.prescriptionPrimary)
            }

            state.results.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.prescription_no_search_results),
                    color = MaterialTheme.extendedColors.prescriptionSupporting,
                )
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.results, key = { it.id }) { medicine ->
                    MedicinePickerCard(medicine) {
                        onIntent(PrescriptionUIIntent.MedicineSelected(medicine.id))
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicinePickerCard(medicine: Medicine, onClick: () -> Unit) {
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
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(colors.prescriptionSuccessSoft, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Medication,
                contentDescription = null,
                tint = colors.prescriptionPrimary,
                modifier = Modifier.size(32.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = medicine.name, color = colors.prescriptionTitle, fontWeight = FontWeight.Bold)
            Text(
                text = medicine.packDescription,
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.prescription_price_egp, medicine.unitPriceEgp),
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
