package com.medsy.presentation.prescription.prescriptionreview.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
internal fun ReviewSummaryCard(
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
                count = state.confirmedCount,
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
