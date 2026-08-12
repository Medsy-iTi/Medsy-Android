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
import com.medsy.domain.orders.model.FulfillmentMethod
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.presentation.R

@Composable
fun OrderDetailsStatusHeader(
    status: OrderStatus,
    fulfillmentMethod: FulfillmentMethod?,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (status) {
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
        OrderStatus.DELIVERED -> MaterialTheme.extendedColors.success
        else -> MaterialTheme.colorScheme.primary
    }
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(status.labelRes()),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = statusColor,
            )
            FulfillmentBadge(fulfillmentMethod)
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        )
        OrderStatusStepper(status, fulfillmentMethod)
    }
}

@Composable
private fun FulfillmentBadge(method: FulfillmentMethod?) {
    val pickup = method == FulfillmentMethod.PICKUP
    Row(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (pickup) Icons.Outlined.Storefront else Icons.Outlined.LocalShipping,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(18.dp),
        )
        Text(
            stringResource(
                if (pickup) R.string.order_details_fulfillment_pickup
                else R.string.order_details_fulfillment_delivery,
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Composable
private fun OrderStatusStepper(status: OrderStatus, fulfillmentMethod: FulfillmentMethod?) {
    val cancelled = status == OrderStatus.CANCELLED
    val steps = if (cancelled) {
        listOf(R.string.orders_status_placed, R.string.orders_status_cancelled)
    } else if (fulfillmentMethod == FulfillmentMethod.PICKUP) {
        listOf(
            R.string.orders_status_placed,
            R.string.order_status_preparing,
            R.string.order_status_ready_for_pickup,
            R.string.order_status_delivered,
        )
    } else {
        listOf(
            R.string.orders_status_placed,
            R.string.orders_status_confirmed,
            R.string.orders_status_delivering,
            R.string.orders_status_delivered,
        )
    }
    val currentStep = when (status) {
        OrderStatus.PENDING, OrderStatus.PENDING_PAYMENT -> 0
        OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP, OrderStatus.READY_FOR_DELIVERY -> 1
        OrderStatus.OUT_FOR_DELIVERY -> 2
        OrderStatus.DELIVERED -> 3
        OrderStatus.CANCELLED -> 1
        OrderStatus.UNKNOWN -> 0
    }
    Box(Modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).align(Alignment.TopCenter)
                .padding(top = 13.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 2.dp,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            steps.forEachIndexed { index, label ->
                val completed = index <= currentStep
                val cancelledStep = cancelled && index == 1
                val color = when {
                    cancelledStep -> MaterialTheme.colorScheme.error
                    completed -> MaterialTheme.extendedColors.success
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(26.dp).clip(CircleShape)
                            .background(if (completed) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                            .border(2.dp, color, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (completed) {
                            Icon(
                                if (cancelledStep) Icons.Default.Close else Icons.Default.Check,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(14.dp),
                            )
                        } else {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
                        }
                    }
                    Text(
                        stringResource(label),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (index == currentStep) FontWeight.Bold else FontWeight.Medium,
                        color = if (index == currentStep) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private fun OrderStatus.labelRes(): Int = when (this) {
    OrderStatus.PENDING -> R.string.order_status_pending
    OrderStatus.PENDING_PAYMENT -> R.string.order_status_pending_payment
    OrderStatus.PREPARING -> R.string.order_status_preparing
    OrderStatus.READY_FOR_PICKUP -> R.string.order_status_ready_for_pickup
    OrderStatus.READY_FOR_DELIVERY -> R.string.order_status_ready_for_delivery
    OrderStatus.OUT_FOR_DELIVERY -> R.string.order_status_out_for_delivery
    OrderStatus.DELIVERED -> R.string.order_status_delivered
    OrderStatus.CANCELLED -> R.string.order_status_cancelled
    OrderStatus.UNKNOWN -> R.string.order_status_unknown
}
