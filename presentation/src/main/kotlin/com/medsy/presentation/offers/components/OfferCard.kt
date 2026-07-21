package com.medsy.presentation.offers.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.offers.model.OfferType
import com.medsy.presentation.offers.model.PharmacyOffer

@Composable
fun OfferCard(
    offer: PharmacyOffer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.offers_from_egp, offer.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            // Pharmacy Details Section
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.offers_pharmacy_title_format, offer.pharmacyName),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = getOfferSubtitle(offer.type),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Badge Section
            Badge(type = offer.type)
        }
    }
}

@Composable
private fun getOfferSubtitle(type: OfferType): String {
    return when (type) {
        OfferType.FULL -> stringResource(R.string.offers_best_price)
        OfferType.PARTIAL -> stringResource(R.string.offers_missing_medicines)
        OfferType.COMBINED -> stringResource(R.string.offers_combined_coverage)
    }
}

@Composable
private fun Badge(type: OfferType) {
    val backgroundColor = when (type) {
        OfferType.FULL -> Color(0xFF0D3B2E) // Dark Green
        OfferType.PARTIAL -> Color(0xFF4A3414) // Dark Orange
        OfferType.COMBINED -> Color(0xFF2A1C3D) // Dark Purple
    }
    val textColor = when (type) {
        OfferType.FULL -> Color(0xFF34D399) // Light Green
        OfferType.PARTIAL -> Color(0xFFFBBF24) // Light Orange
        OfferType.COMBINED -> Color(0xFFA78BFA) // Light Purple
    }
    val textRes = when (type) {
        OfferType.FULL -> R.string.offers_badge_full
        OfferType.PARTIAL -> R.string.offers_badge_partial
        OfferType.COMBINED -> R.string.offers_badge_combined
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
