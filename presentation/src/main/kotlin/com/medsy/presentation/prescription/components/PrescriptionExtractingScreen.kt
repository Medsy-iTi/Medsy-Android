package com.medsy.presentation.prescription.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
fun PrescriptionExtractingScreen(onIntent: (PrescriptionUIIntent) -> Unit) {
    val colors = MaterialTheme.extendedColors
    Column(modifier = Modifier.fillMaxSize()) {
        PrescriptionAppBar(
            title = stringResource(R.string.prescription_reading_title),
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
                text = stringResource(R.string.prescription_extracting_heading),
                color = colors.prescriptionTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.prescription_extracting_description),
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Text(
                    text = stringResource(R.string.prescription_stage_quality),
                    color = colors.prescriptionSupporting,
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
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

@Composable
private fun LoadingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "prescriptionLoading")
    val opacity by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "prescriptionLoadingOpacity",
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .alpha((opacity - index * 0.12f).coerceIn(0.25f, 1f))
                    .background(MaterialTheme.extendedColors.prescriptionPrimary, CircleShape),
            )
        }
    }
}
