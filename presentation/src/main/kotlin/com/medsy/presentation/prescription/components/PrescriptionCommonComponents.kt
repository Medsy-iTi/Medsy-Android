package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R

@Composable
internal fun PrescriptionAppBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = MaterialTheme.extendedColors.prescriptionTitle,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.prescriptionTitle,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
internal fun PrescriptionPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(if (enabled) Modifier.shadow(12.dp, RoundedCornerShape(16.dp)) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.extendedColors.prescriptionPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.extendedColors.prescriptionDisabledContainer,
            disabledContentColor = MaterialTheme.extendedColors.prescriptionSupporting,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
internal fun PrescriptionTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = MaterialTheme.extendedColors.prescriptionPrimary,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
    )
}

@Composable
internal fun RxDocumentIllustration(
    modifier: Modifier = Modifier,
    showSearch: Boolean = false,
) {
    val colors = MaterialTheme.extendedColors
    Box(
        modifier = modifier
            .size(192.dp)
            .background(colors.prescriptionSuccessSoft.copy(alpha = 0.72f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 92.dp, height = 124.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                .border(2.dp, colors.prescriptionScanBorder, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (showSearch) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = colors.prescriptionPrimary,
                    modifier = Modifier.size(54.dp),
                )
            } else {
                Text(
                    text = "Rx",
                    color = colors.prescriptionPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(40.dp)
                .background(colors.prescriptionPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (showSearch) Icons.Filled.Check else Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
internal fun StatusPill(
    text: String,
    isWarning: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.extendedColors
    Row(
        modifier = modifier
            .background(
                if (isWarning) colors.prescriptionWarningContainer else colors.prescriptionSuccessContainer,
                RoundedCornerShape(50),
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (isWarning) Icons.Filled.Search else Icons.Filled.Check,
            contentDescription = null,
            tint = if (isWarning) colors.prescriptionWarningContent else colors.prescriptionSuccessContent,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = text,
            color = if (isWarning) colors.prescriptionWarningContent else colors.prescriptionSuccessContent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
