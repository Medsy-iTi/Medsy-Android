package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.HorizontalDivider
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
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.model.FulfillmentType
import com.medsy.presentation.orders.orderslist.model.OrderStatus

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(statusLabelRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            OrderFulfillmentBadge(fulfillmentType = fulfillmentType)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )

        OrderStatusStepper(status = status)
    }
}

@Composable
private fun OrderStatusStepper(
    status: OrderStatus,
    modifier: Modifier = Modifier,
) {
    val steps = if (status == OrderStatus.Cancelled) {
        listOf(
            R.string.orders_status_placed to true,
            R.string.orders_status_cancelled to false
        )
    } else {
        listOf(
            R.string.orders_status_placed to true,
            R.string.orders_status_confirmed to true,
            R.string.orders_status_delivering to true,
            R.string.orders_status_delivered to true
        )
    }

    val currentStepIndex = when (status) {
        OrderStatus.Cancelled -> 1
        OrderStatus.Confirmed -> 1
        OrderStatus.Delivered -> 3
    }

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.TopCenter)
                .padding(top = 13.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 2.dp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            steps.forEachIndexed { index, (labelRes, isValidStep) ->
                val isCompleted = index <= currentStepIndex
                val isActive = index == currentStepIndex
                val isCancelledStep = !isValidStep

                val stepColor = when {
                    isCancelledStep -> MaterialTheme.colorScheme.error
                    isCompleted -> MaterialTheme.extendedColors.success
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted || isCancelledStep) stepColor.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 2.dp,
                                color = stepColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCancelledStep) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = stepColor,
                                modifier = Modifier.size(14.dp)
                            )
                        } else if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = stepColor,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(stepColor)
                            )
                        }
                    }

                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 1
                    )
                }
            }
        }
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
