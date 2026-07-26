package com.medsy.presentation.prescription.prescriptionreview.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.prescription.model.ExtractedMedicine
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionUIIntent
import com.medsy.presentation.prescription.components.PrescriptionPrimaryButton
import com.medsy.presentation.prescription.components.StatusPill


@Composable
internal fun ExtractedMedicineCard(
    item: ExtractedMedicine,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    val needsReview = !item.isConfirmed
    val borderColor =
        if (needsReview) colors.prescriptionWarningBorder else MaterialTheme.colorScheme.outline

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (needsReview) colors.prescriptionWarningContainer else colors.prescriptionSuccessSoft,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusPill(
                text = stringResource(
                    if (needsReview) R.string.prescription_needs_review else R.string.prescription_recognized,
                ),
                isWarning = needsReview,
                modifier = Modifier.weight(1f, fill = false),
            )
            Spacer(modifier = Modifier.weight(1f))

            if (needsReview) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.prescription_search_medicine_hint),
                    tint = colors.prescriptionPrimary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onIntent(PrescriptionUIIntent.EditMedicineClicked(item.localItemId)) }
                        .padding(6.dp),
                )
                Spacer(modifier = Modifier.size(4.dp))
            }

            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = stringResource(R.string.prescription_delete_medicine_description),
                tint = colors.prescriptionSupporting,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onIntent(PrescriptionUIIntent.DeleteMedicineClicked(item.localItemId))
                    }
                    .padding(6.dp),
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (needsReview) {
                Text(
                    text = stringResource(R.string.prescription_unclear_medicine_message),
                    color = colors.prescriptionSupporting,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (item.selectedMedicine != null) {
                MedicineIdentityRow(item)
                if (needsReview) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallActionButton(
                            text = stringResource(R.string.prescription_confirm_medicine),
                            primary = true,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onIntent(PrescriptionUIIntent.ConfirmMedicineClicked(item.localItemId))
                            },
                        )
                        SmallActionButton(
                            text = stringResource(R.string.prescription_choose_another),
                            primary = false,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onIntent(PrescriptionUIIntent.EditMedicineClicked(item.localItemId))
                            },
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        QuantityControl(item, onIntent)
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onIntent(PrescriptionUIIntent.EditMedicineClicked(item.localItemId))
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = stringResource(R.string.prescription_edit),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = item.extractedName ?: item.rawText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.prescriptionTitle
                    )
                    Text(
                        text = stringResource(R.string.prescription_no_search_results),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.prescriptionSupporting,
                        textAlign = TextAlign.Center
                    )
                    PrescriptionPrimaryButton(
                        text = stringResource(R.string.prescription_search_medicine_hint),
                        onClick = { onIntent(PrescriptionUIIntent.EditMedicineClicked(item.localItemId)) }
                    )
                }
            }
        }
    }
}
