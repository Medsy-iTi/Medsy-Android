package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.model.FulfillmentType
import com.medsy.presentation.orders.model.OrderStatus

@Composable
fun OrderDetailsStatusHeader(
    status: OrderStatus,
    dateLabel: String,
    fulfillmentType: FulfillmentType,
    modifier: Modifier = Modifier,
) {
    val (statusLabelRes, statusColor) = when (status) {
        OrderStatus.Confirmed -> R.string.orders_status_confirmed to MaterialTheme.extendedColors.success
        OrderStatus.Delivered -> R.string.orders_status_delivered to MaterialTheme.extendedColors.success
        OrderStatus.Cancelled -> R.string.orders_status_cancelled to MaterialTheme.colorScheme.error
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(statusLabelRes),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = statusColor,
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        OrderFulfillmentBadge(fulfillmentType = fulfillmentType)
    }
}

@Composable
private fun OrderFulfillmentBadge(
    fulfillmentType: FulfillmentType,
    modifier: Modifier = Modifier,
) {
    val (icon, labelRes) = when (fulfillmentType) {
        FulfillmentType.Pickup -> Icons.Outlined.Storefront to R.string.order_details_fulfillment_pickup
        FulfillmentType.Delivery -> Icons.Outlined.LocalShipping to R.string.order_details_fulfillment_delivery
    }

    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(end = 6.dp),
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}
