package com.medsy.presentation.prescription.prescriptionsource


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.CircularProgressIndicator
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
import com.medsy.presentation.R
import com.medsy.presentation.prescription.PrescriptionState
import com.medsy.presentation.prescription.PrescriptionUIIntent
import com.medsy.presentation.prescription.components.CameraSourceIcon
import com.medsy.presentation.prescription.components.GallerySourceIcon
import com.medsy.presentation.prescription.components.PrescriptionAppBar
import com.medsy.presentation.prescription.components.RxDocumentIllustration
import com.medsy.presentation.prescription.prescriptionsource.components.SourceOptionCard
import com.medsy.presentation.prescription.prescriptionimagepreview.components.PrescriptionTipsCard

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
                text = stringResource(
                    if (state.isAttachmentOnly) {
                        R.string.prescription_attachment_heading
                    } else {
                        R.string.prescription_upload_heading
                    }
                ),
                color = MaterialTheme.extendedColors.prescriptionTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(
                    if (state.isAttachmentOnly) {
                        R.string.prescription_attachment_description
                    } else {
                        R.string.prescription_upload_description
                    }
                ),
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
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

