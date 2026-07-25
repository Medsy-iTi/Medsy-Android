package com.medsy.presentation.home.components.activesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.home.ActiveSearchStatus

@Composable
fun ActiveSearchTimer(status: ActiveSearchStatus) {
    val remainingTime = when (status) {
        is ActiveSearchStatus.Searching -> status.remainingTimeSeconds
        is ActiveSearchStatus.FirstOfferArrived -> status.remainingTimeSeconds
        is ActiveSearchStatus.MultipleOffersArrived -> status.remainingTimeSeconds
        else -> 0
    }
    
    val minutes = remainingTime / 60
    val seconds = remainingTime % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    
    val isExpiringSoon = remainingTime <= 60

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Schedule,
                contentDescription = null,
            tint = if (isExpiringSoon) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_search_status_time),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isExpiringSoon) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Text(
            text = timeString,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isExpiringSoon) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
