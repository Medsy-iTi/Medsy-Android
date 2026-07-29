package com.medsy.presentation.orders.orderslist.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder
import com.medsy.presentation.R
import com.medsy.presentation.orders.orderslist.model.OrderFilter

@Composable
fun OrdersFilterChipsRow(
    selectedFilter: OrderFilter,
    onFilterSelected: (OrderFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = listOf(
        OrderFilter.All to R.string.orders_filter_all,
        OrderFilter.Active to R.string.orders_filter_active,
        OrderFilter.Finished to R.string.orders_filter_finished,
        OrderFilter.Cancelled to R.string.orders_filter_cancelled,
    )

    LazyRow(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters, key = { it.first }) { (filter, labelRes) ->
            OrdersFilterChip(
                label = stringResource(labelRes),
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
            )
        }
    }
}

@Composable
fun OrdersFilterChipsShimmerRow(
    modifier: Modifier = Modifier,
) {
    MedsyShimmer(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val widths = listOf(56.dp, 76.dp, 88.dp, 80.dp)
            widths.forEach { width ->
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .width(width)
                        .height(34.dp),
                    shape = RoundedCornerShape(50.dp)
                )
            }
        }
    }
}

@Composable
private fun OrdersFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "ChipBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "ChipContent"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "ChipBorder"
    )

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50.dp))
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .clip(RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
        )
    }
}
