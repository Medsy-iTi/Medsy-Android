package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun OrderDetailsShimmer(modifier: Modifier = Modifier) {
    MedsyShimmer(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MedsyShimmerPlaceholder(Modifier.fillMaxWidth().height(104.dp))
            MedsyShimmerPlaceholder(Modifier.fillMaxWidth().height(80.dp))
            MedsyShimmerPlaceholder(Modifier.fillMaxWidth().height(220.dp))
            MedsyShimmerPlaceholder(Modifier.fillMaxWidth().height(140.dp))
        }
    }
}
