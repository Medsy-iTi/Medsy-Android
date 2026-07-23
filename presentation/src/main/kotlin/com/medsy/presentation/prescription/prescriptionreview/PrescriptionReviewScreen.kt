package com.medsy.presentation.prescription.prescriptionreview


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent
import com.medsy.presentation.prescription.components.PrescriptionAppBar
import com.medsy.presentation.prescription.components.PrescriptionPrimaryButton
import com.medsy.presentation.prescription.components.PrescriptionTextAction
import com.medsy.presentation.prescription.prescriptionreview.components.ExtractedMedicineCard
import com.medsy.presentation.prescription.prescriptionreview.components.ReviewSummaryCard
import com.medsy.presentation.prescription.prescriptionreview.components.ReviewWarning

@Composable
fun PrescriptionReviewScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    val isMedicineSearch = state.isMedicineSearch
    Column {
        PrescriptionAppBar(
            title = stringResource(
                if (isMedicineSearch) R.string.search_results_title else R.string.prescription_review_title
            ),
            onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.prescription_review_description),
                    color = MaterialTheme.extendedColors.prescriptionSupporting,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            item { ReviewSummaryCard(state, onIntent) }

            items(state.medicines, key = { it.localItemId }) { medicine ->
                ExtractedMedicineCard(medicine, onIntent)
            }

            if (state.needsReviewCount > 0) {
                item { ReviewWarning() }
            }

            item {
                PrescriptionPrimaryButton(
                    text = stringResource(
                        if (isMedicineSearch) R.string.product_add_to_cart
                        else R.string.prescription_add_to_cart
                    ),
                    enabled = state.canSubmit,
                    onClick = { onIntent(PrescriptionUIIntent.AddToCartClicked) },
                )
            }

            item {
                PrescriptionTextAction(
                    text = stringResource(R.string.prescription_add_medicine_manually),
                    onClick = { onIntent(PrescriptionUIIntent.AddMedicineManuallyClicked) },
                )
            }
        }
    }
}

