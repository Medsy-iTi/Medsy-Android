package com.medsy.presentation.prescription.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
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
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.extendedColors.prescriptionPrimary)
            }

            state.results.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.prescription_no_search_results),
                    color = MaterialTheme.extendedColors.prescriptionSupporting,
                )
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.results, key = { it.productId }) { medicine ->
                    MedicinePickerCard(medicine) {
                        onIntent(PrescriptionUIIntent.MedicineSelected(medicine.productId))
                    }
                }
            }
        }
    }
}

