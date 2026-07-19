package com.medsy.presentation.home.components.activesearch

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.home.ActiveSearchStatus

@Composable
fun ActiveSearchHeader(
    status: ActiveSearchStatus,
    onCancelClick: () -> Unit
) {
    val titleRes: Int
    val descRes: Int
    val icon: @Composable () -> Unit
    val iconBgColor: Color
    
    when (status) {
        is ActiveSearchStatus.Searching -> {
            titleRes = R.string.home_search_status_searching_title
            descRes = R.string.home_search_status_searching_desc
            iconBgColor = MaterialTheme.colorScheme.primary
            icon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)) }
        }
        is ActiveSearchStatus.FirstOfferArrived -> {
            titleRes = R.string.home_search_status_first_offer_title
            descRes = R.string.home_search_status_first_offer_desc
            iconBgColor = MaterialTheme.colorScheme.primary
            icon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)) }
        }
        is ActiveSearchStatus.MultipleOffersArrived -> {
            titleRes = R.string.home_search_status_multiple_offers_title
            descRes = R.string.home_search_status_multiple_offers_desc
            iconBgColor = MaterialTheme.colorScheme.primary
            icon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)) }
        }
        is ActiveSearchStatus.SearchEndedNoOffers -> {
            titleRes = R.string.home_search_status_ended_title
            descRes = R.string.home_search_status_ended_desc
            iconBgColor = MaterialTheme.colorScheme.surfaceVariant
            icon = { Icon(Icons.Rounded.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp)) }
        }
        ActiveSearchStatus.Idle -> return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        val isSearching = status is ActiveSearchStatus.Searching
        val infiniteTransition = rememberInfiniteTransition(label = "icon_anim")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isSearching) 1.15f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_scale"
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(descRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = onCancelClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.home_search_status_cancel),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
