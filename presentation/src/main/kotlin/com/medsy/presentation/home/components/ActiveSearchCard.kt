package com.medsy.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.presentation.home.ActiveSearchStatus
import com.medsy.presentation.home.components.activesearch.ActiveSearchContent
import com.medsy.presentation.home.components.activesearch.ActiveSearchHeader
import com.medsy.presentation.home.components.activesearch.ActiveSearchTimer

@Composable
fun ActiveSearchCard(
    status: ActiveSearchStatus,
    onCancelClick: () -> Unit,
    onViewOffersClick: () -> Unit,
    onSearchWiderRangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (status is ActiveSearchStatus.Idle) return

    val isEnded = status is ActiveSearchStatus.SearchEndedNoOffers

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ActiveSearchHeader(status = status, onCancelClick = onCancelClick)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!isEnded) {
                ActiveSearchTimer(status = status)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            ActiveSearchContent(
                status = status,
                onViewOffersClick = onViewOffersClick,
                onSearchWiderRangeClick = onSearchWiderRangeClick,
                onCancelClick = onCancelClick
            )
        }
    }
}
