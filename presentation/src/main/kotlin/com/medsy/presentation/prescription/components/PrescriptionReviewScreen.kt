package com.medsy.presentation.prescription.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.prescription.model.PrescriptionMedicine
import com.medsy.domain.prescription.model.RecognitionStatus
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
fun PrescriptionReviewScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    Column {
        PrescriptionAppBar(
            title = stringResource(R.string.prescription_review_title),
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
            items(state.medicines, key = { it.medicine.id }) { medicine ->
                ExtractedMedicineCard(medicine, onIntent)
            }
            if (state.needsReviewCount > 0) {
                item { ReviewWarning() }
            }
            item {
                PrescriptionPrimaryButton(
                    text = stringResource(R.string.prescription_add_to_cart),
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

@Composable
private fun ReviewSummaryCard(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.prescription_found_count, state.medicines.size),
            color = colors.prescriptionTitle,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryCountChip(
                count = state.recognizedCount,
                label = stringResource(R.string.prescription_recognized_count),
                warning = false,
                modifier = Modifier.weight(1f),
            )
            SummaryCountChip(
                count = state.needsReviewCount,
                label = stringResource(R.string.prescription_needs_review_count),
                warning = true,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    colors.prescriptionPrimary.copy(alpha = 0.3f),
                    RoundedCornerShape(14.dp),
                )
                .clickable { onIntent(PrescriptionUIIntent.TogglePrescriptionImageClicked) }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.prescription_show_image),
                color = colors.prescriptionPrimary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (state.isPrescriptionExpanded) {
                    Icons.Filled.ExpandLess
                } else {
                    Icons.Filled.ExpandMore
                },
                contentDescription = null,
                tint = colors.prescriptionPrimary,
            )
        }
        if (state.isPrescriptionExpanded) {
            AsyncImage(
                model = state.image?.uri?.let(Uri::parse),
                contentDescription = stringResource(R.string.prescription_image_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.prescriptionSuccessContainer),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun SummaryCountChip(
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

@Composable
private fun ExtractedMedicineCard(
    item: PrescriptionMedicine,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    val needsReview = item.recognitionStatus == RecognitionStatus.NEEDS_REVIEW
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
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = stringResource(R.string.prescription_delete_medicine_description),
                tint = colors.prescriptionSupporting,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onIntent(PrescriptionUIIntent.DeleteMedicineClicked(item.medicine.id))
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
            MedicineIdentityRow(item)
            if (needsReview) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallActionButton(
                        text = stringResource(R.string.prescription_confirm_medicine),
                        primary = true,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onIntent(PrescriptionUIIntent.ConfirmMedicineClicked(item.medicine.id))
                        },
                    )
                    SmallActionButton(
                        text = stringResource(R.string.prescription_choose_another),
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onIntent(PrescriptionUIIntent.EditMedicineClicked(item.medicine.id))
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
                                onIntent(PrescriptionUIIntent.EditMedicineClicked(item.medicine.id))
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
        }
    }
}

@Composable
private fun MedicineIdentityRow(item: PrescriptionMedicine) {
    val colors = MaterialTheme.extendedColors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = item.medicine.imageUrl,
            contentDescription = item.medicine.name,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.prescriptionSuccessSoft),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
            error = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.medicine.name,
                color = colors.prescriptionTitle,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = item.medicine.packDescription,
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.prescription_price_egp, item.medicine.unitPriceEgp),
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

@Composable
private fun QuantityControl(
    item: PrescriptionMedicine,
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
                onIntent(PrescriptionUIIntent.DecreaseQuantityClicked(item.medicine.id))
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
                onIntent(PrescriptionUIIntent.IncreaseQuantityClicked(item.medicine.id))
            },
        )
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                if (primary) {
                    MaterialTheme.extendedColors.prescriptionPrimary
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                },
                RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (primary) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.extendedColors.prescriptionTitle
            },
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun SmallActionButton(
    text: String,
    primary: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    Text(
        text = text,
        color = if (primary) MaterialTheme.colorScheme.onPrimary else colors.prescriptionTitle,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(
                if (primary) {
                    colors.prescriptionPrimary
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                },
                RoundedCornerShape(14.dp),
            )
            .border(
                1.dp,
                if (primary) colors.prescriptionPrimary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
    )
}

@Composable
private fun ReviewWarning() {
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
