package com.medsy.presentation.productdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R

@Composable
fun PharmacistNoticeCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.extendedColors.warningContainer)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.product_details_pharmacist_notice),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.onWarningContainer,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Filled.VerifiedUser,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
