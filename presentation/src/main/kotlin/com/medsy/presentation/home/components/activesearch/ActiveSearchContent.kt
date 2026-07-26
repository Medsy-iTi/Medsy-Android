package com.medsy.presentation.home.components.activesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.home.ActiveSearchStatus

@Composable
fun ActiveSearchContent(
    status: ActiveSearchStatus,
    onViewOffersClick: () -> Unit,
    onSearchWiderRangeClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    when (status) {
        is ActiveSearchStatus.Searching -> {
            val stage = when {
                status.remainingTimeSeconds > 600 -> 1
                status.remainingTimeSeconds > 300 -> 2
                else -> 3
            }
            ActiveSearchStages(currentStage = stage)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.home_search_status_notification_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        is ActiveSearchStatus.FirstOfferArrived -> {
            ActiveSearchOfferSummary(minPrice = status.minPrice, foundCount = status.foundCount, totalCount = status.totalCount, onViewOffersClick = onViewOffersClick) 
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.home_search_status_auto_stop_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        is ActiveSearchStatus.MultipleOffersArrived -> {
            ActiveSearchOfferSummary(minPrice = status.minPrice, foundCount = status.foundCount, totalCount = status.totalCount, onViewOffersClick = onViewOffersClick)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_search_status_offers_available_format, status.totalOffers),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.home_search_status_auto_stop_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        is ActiveSearchStatus.SearchEndedNoOffers -> {
            Button(
                onClick = onSearchWiderRangeClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurfaceVariant) 
            ) {
                Text(
                    text = stringResource(R.string.home_search_status_search_wider),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onCancelClick, 
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.home_search_status_cancel),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {}
    }
}
