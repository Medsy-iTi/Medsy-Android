package com.medsy.presentation.prescription.prescriptionimagepreview

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
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
import com.medsy.presentation.prescription.components.PrescriptionAppBar
import com.medsy.presentation.prescription.components.PrescriptionPrimaryButton
import com.medsy.presentation.prescription.prescriptionimagepreview.components.PrescriptionTipsCard

@Composable
fun PrescriptionImagePreviewScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    val colors = MaterialTheme.extendedColors
    Column(modifier = Modifier.fillMaxSize()) {
        PrescriptionAppBar(
            title = stringResource(R.string.prescription_upload_title),
            onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(226.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.prescriptionSuccessContainer)
                    .border(1.dp, colors.prescriptionScanBorder, RoundedCornerShape(24.dp)),
            ) {
                AsyncImage(
                    model = state.image?.uri?.let(Uri::parse),
                    contentDescription = stringResource(R.string.prescription_image_content_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.prescriptionSuccessContent.copy(alpha = 0.9f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.prescription_image_selected_success),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .clickable { onIntent(PrescriptionUIIntent.ChangeImageClicked) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.prescription_change_image),
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    modifier = Modifier
                        .height(44.dp)
                        .background(colors.prescriptionErrorContainer, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            colors.prescriptionErrorContent.copy(alpha = 0.35f),
                            RoundedCornerShape(16.dp),
                        )
                        .clickable { onIntent(PrescriptionUIIntent.DeleteImageClicked) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = null,
                        tint = colors.prescriptionErrorContent,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = stringResource(R.string.prescription_delete),
                        color = colors.prescriptionErrorContent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            PrescriptionTipsCard(showThreeTips = false)
            PrescriptionPrimaryButton(
                text = stringResource(
                    if (state.isAttachmentOnly) {
                        R.string.prescription_attach_to_cart
                    } else {
                        R.string.prescription_review_image
                    }
                ),
                onClick = { onIntent(PrescriptionUIIntent.ReviewImageClicked) },
                enabled = !state.isSubmitting,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

