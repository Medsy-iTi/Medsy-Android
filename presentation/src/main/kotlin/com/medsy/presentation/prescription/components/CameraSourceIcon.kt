package com.medsy.presentation.prescription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors

@Composable
fun CameraSourceIcon() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(
                MaterialTheme.extendedColors.prescriptionSuccessSoft,
                RoundedCornerShape(16.dp)
            ),
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

