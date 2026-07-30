package com.medsy.presentation.prescription.prescriptionextracting


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent
import com.medsy.presentation.prescription.prescriptionextracting.components.LoadingDots
import com.medsy.presentation.prescription.components.PrescriptionAppBar
import com.medsy.presentation.prescription.components.RxDocumentIllustration

@Composable
fun PrescriptionExtractingScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit
) {
    val colors = MaterialTheme.extendedColors
    Column(modifier = Modifier.fillMaxSize()) {
        PrescriptionAppBar(
            title = stringResource(
                if (state.isMedicineSearch) R.string.home_card_search_title else R.string.prescription_reading_title
            ),
            onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RxDocumentIllustration(modifier = Modifier.size(208.dp), showSearch = true)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(
                    if (state.isMedicineSearch) R.string.home_card_search_title else R.string.prescription_extracting_heading
                ),
                color = colors.prescriptionTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(
                    if (state.isMedicineSearch) R.string.home_promo_subtitle_one else R.string.prescription_extracting_description
                ),
                color = colors.prescriptionSupporting,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = stringResource(R.string.prescription_extracting_wait),
                color = colors.prescriptionSupporting.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            LoadingDots(modifier = Modifier.padding(vertical = 24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.prescriptionSuccessContainer, RoundedCornerShape(16.dp))
                    .border(1.dp, colors.prescriptionScanBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CenterFocusStrong,
                        contentDescription = null,
                        tint = colors.prescriptionPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.prescription_reading_stages),
                        color = colors.prescriptionPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = stringResource(R.string.prescription_stage_uploaded),
                    color = colors.prescriptionSupporting,
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Text(
                    text = stringResource(R.string.prescription_stage_quality),
                    color = colors.prescriptionSupporting,
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Text(
                    text = stringResource(R.string.prescription_stage_extracting),
                    color = colors.prescriptionPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

