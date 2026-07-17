package com.medsy.presentation.prescription.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent

@Composable
fun PrescriptionSourceScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PrescriptionAppBar(
            title = stringResource(R.string.prescription_upload_title),
            onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RxDocumentIllustration()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.prescription_upload_heading),
                color = MaterialTheme.extendedColors.prescriptionTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.prescription_upload_description),
                color = MaterialTheme.extendedColors.prescriptionSupporting,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, start = 28.dp, end = 28.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            SourceOptionCard(
                title = stringResource(R.string.prescription_take_photo),
                subtitle = stringResource(R.string.prescription_take_photo_description),
                icon = { CameraSourceIcon() },
                enabled = !state.isPreparingImage,
                onClick = { onIntent(PrescriptionUIIntent.CameraClicked) },
            )
            Spacer(modifier = Modifier.height(12.dp))
            SourceOptionCard(
                title = stringResource(R.string.prescription_choose_gallery),
                subtitle = stringResource(R.string.prescription_choose_gallery_description),
                icon = { GallerySourceIcon() },
                enabled = !state.isPreparingImage,
                onClick = { onIntent(PrescriptionUIIntent.GalleryClicked) },
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrescriptionTipsCard(showThreeTips = true)
            Spacer(modifier = Modifier.height(24.dp))
            if (state.isPreparingImage) {
                CircularProgressIndicator(
                    color = MaterialTheme.extendedColors.prescriptionPrimary,
                    modifier = Modifier.size(28.dp),
                )
            }
            PrescriptionPrimaryButton(
                text = stringResource(R.string.prescription_continue),
                onClick = {},
                enabled = false,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

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
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(17.dp))
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
                text = stringResource(R.string.prescription_review_image),
                onClick = { onIntent(PrescriptionUIIntent.ReviewImageClicked) },
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SourceOptionCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        icon()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.extendedColors.prescriptionTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.extendedColors.prescriptionSupporting,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.prescriptionSupporting,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun CameraSourceIcon() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(MaterialTheme.extendedColors.prescriptionSuccessSoft, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.CameraAlt,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.prescriptionPrimary,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun GallerySourceIcon() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(MaterialTheme.extendedColors.prescriptionGalleryContainer, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Image,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.prescriptionGalleryContent,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun PrescriptionTipsCard(showThreeTips: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(
                if (showThreeTips) R.string.prescription_best_result else R.string.prescription_quick_reminder,
            ),
            color = if (showThreeTips) {
                MaterialTheme.extendedColors.prescriptionTitle
            } else {
                MaterialTheme.extendedColors.prescriptionSupporting
            },
            fontWeight = FontWeight.Bold,
        )
        TipRow(Icons.Filled.CenterFocusStrong, stringResource(R.string.prescription_tip_clear_names))
        TipRow(Icons.Filled.CameraAlt, stringResource(R.string.prescription_tip_avoid_shadows))
        if (showThreeTips) {
            TipRow(Icons.Filled.CropFree, stringResource(R.string.prescription_tip_full_image))
        }
    }
}

@Composable
private fun TipRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.extendedColors.prescriptionSuccessSoft, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.prescriptionPrimary,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = text,
            color = MaterialTheme.extendedColors.prescriptionSupporting,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
