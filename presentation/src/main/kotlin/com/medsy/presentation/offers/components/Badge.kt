package com.medsy.presentation.offers.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.offers.model.OfferType

@Composable
fun Badge(type: OfferType) {
    val backgroundColor = when (type) {
        OfferType.FULL -> MaterialTheme.extendedColors.badgeSuccess
        OfferType.PARTIAL -> MaterialTheme.extendedColors.warning
    }
    val textColor = when (type) {
        OfferType.FULL -> MaterialTheme.extendedColors.onBadgeSuccess
        OfferType.PARTIAL -> MaterialTheme.extendedColors.onWarning
    }
    val textRes = when (type) {
        OfferType.FULL -> R.string.offers_badge_full
        OfferType.PARTIAL -> R.string.offers_badge_partial
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
