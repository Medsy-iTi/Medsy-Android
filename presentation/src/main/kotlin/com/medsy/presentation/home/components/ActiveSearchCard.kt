package com.medsy.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.domain.requests.model.ActiveRequestStatus
import com.medsy.presentation.home.components.activesearch.ActiveSearchContent
import com.medsy.presentation.home.components.activesearch.ActiveSearchHeader
import com.medsy.presentation.home.components.activesearch.ActiveSearchTimer

@Composable
fun ActiveSearchCard(
    status: ActiveRequestStatus,
    onViewOffersClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 8.dp)
            .clickable(enabled = status.hasAvailableProducts, onClick = onViewOffersClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ActiveSearchHeader(status)
            Spacer(Modifier.height(16.dp))
            ActiveSearchTimer(status)
            Spacer(Modifier.height(16.dp))
            ActiveSearchContent(status, onViewOffersClick)
        }
    }
}
