package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
fun PrescriptionUploadErrorScreen(onIntent: (PrescriptionUIIntent) -> Unit) {
    PrescriptionFailureLayout(
        appBarTitle = stringResource(R.string.prescription_upload_title),
        icon = Icons.Filled.WifiOff,
        iconType = FailureIconType.ERROR,
        title = stringResource(R.string.prescription_upload_failed_title),
        description = stringResource(R.string.prescription_upload_failed_description),
        primaryText = stringResource(R.string.prescription_retry),
        onPrimary = { onIntent(PrescriptionUIIntent.RetryExtractionClicked) },
        secondaryText = stringResource(R.string.prescription_choose_another_image),
        onSecondary = { onIntent(PrescriptionUIIntent.ChooseAnotherImageClicked) },
        onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
    )
}

@Composable
fun PrescriptionUnreadableScreen(onIntent: (PrescriptionUIIntent) -> Unit) {
    PrescriptionFailureLayout(
        appBarTitle = stringResource(R.string.prescription_read_prescription_title),
        icon = Icons.Filled.ErrorOutline,
        iconType = FailureIconType.WARNING,
        title = stringResource(R.string.prescription_unreadable_title),
        description = stringResource(R.string.prescription_unreadable_description),
        primaryText = stringResource(R.string.prescription_choose_another_image),
        onPrimary = { onIntent(PrescriptionUIIntent.ChooseAnotherImageClicked) },
        onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
    )
}

@Composable
fun PrescriptionNoMedicinesScreen(onIntent: (PrescriptionUIIntent) -> Unit) {
    PrescriptionFailureLayout(
        appBarTitle = stringResource(R.string.prescription_review_title),
        icon = Icons.Filled.SearchOff,
        iconType = FailureIconType.SUCCESS,
        title = stringResource(R.string.prescription_no_medicines_title),
        description = stringResource(R.string.prescription_no_medicines_description),
        primaryText = stringResource(R.string.prescription_choose_another_image),
        onPrimary = { onIntent(PrescriptionUIIntent.ChooseAnotherImageClicked) },
        secondaryText = stringResource(R.string.prescription_add_medicine_manually),
        onSecondary = { onIntent(PrescriptionUIIntent.AddMedicineManuallyClicked) },
        onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
    )
}

private enum class FailureIconType { ERROR, WARNING, SUCCESS }

@Composable
private fun PrescriptionFailureLayout(
    appBarTitle: String,
    icon: ImageVector,
    iconType: FailureIconType,
    title: String,
    description: String,
    primaryText: String,
    onPrimary: () -> Unit,
    onBack: () -> Unit,
    secondaryText: String? = null,
    onSecondary: () -> Unit = {},
) {
    val colors = MaterialTheme.extendedColors
    val container = when (iconType) {
        FailureIconType.ERROR -> colors.prescriptionErrorContainer
        FailureIconType.WARNING -> colors.prescriptionWarningContainer
        FailureIconType.SUCCESS -> colors.prescriptionSuccessSoft
    }
    val content = when (iconType) {
        FailureIconType.ERROR -> colors.prescriptionErrorContent
        FailureIconType.WARNING -> colors.prescriptionWarningContent
        FailureIconType.SUCCESS -> colors.prescriptionPrimary
    }
    Column(modifier = Modifier.fillMaxSize()) {
        PrescriptionAppBar(title = appBarTitle, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(container, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(46.dp)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = title,
                color = colors.prescriptionTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp),
            )
            PrescriptionPrimaryButton(text = primaryText, onClick = onPrimary)
            if (secondaryText != null) {
                PrescriptionTextAction(text = secondaryText, onClick = onSecondary)
            }
        }
    }
}

@Composable
fun PrescriptionConfirmationScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .background(colors.prescriptionSuccessContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .border(3.dp, colors.prescriptionPrimary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = colors.prescriptionPrimary,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(
                if (state.isPartialSubmission) {
                    R.string.prescription_partial_title
                } else {
                    R.string.prescription_confirmation_title
                }
            ),
            color = colors.prescriptionTitle,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(
                if (state.isPartialSubmission) {
                    R.string.prescription_partial_description
                } else {
                    R.string.prescription_confirmation_description
                }
            ),
            color = colors.prescriptionSupporting,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, start = 20.dp, end = 20.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colors.prescriptionSuccessSoft,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.prescription_cart_summary),
                    color = colors.prescriptionPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = colors.prescriptionPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
            state.medicines.forEach { item ->
                val medicineName = item.selectedMedicine?.name ?: item.extractedName ?: item.rawText
                val price = item.selectedMedicine?.price ?: 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (item.quantity == 1) {
                            medicineName
                        } else {
                            stringResource(
                                R.string.prescription_medicine_with_quantity,
                                medicineName,
                                item.quantity,
                            )
                        },
                        color = colors.prescriptionTitle,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(
                            R.string.prescription_price_egp,
                            price * item.quantity,
                        ),
                        color = colors.prescriptionPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.prescriptionSuccessContainer.copy(alpha = 0.45f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = stringResource(R.string.prescription_total),
                    color = colors.prescriptionTitle,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(
                        R.string.prescription_price_egp,
                        state.totalPriceEgp,
                    ),
                    color = colors.prescriptionPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        PrescriptionPrimaryButton(
            text = stringResource(R.string.prescription_view_cart),
            onClick = { onIntent(PrescriptionUIIntent.ViewCartClicked) },
        )
        PrescriptionTextAction(
            text = stringResource(R.string.prescription_return_home),
            onClick = { onIntent(PrescriptionUIIntent.ReturnHomeClicked) },
        )
    }
}
