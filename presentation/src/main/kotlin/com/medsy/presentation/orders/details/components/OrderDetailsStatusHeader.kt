package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(20.dp)
            )
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
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(20.dp)
            )
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
        listOf(
            TimelineStep(R.string.orders_timeline_offered, Icons.Outlined.LocalOffer),
            TimelineStep(R.string.orders_status_cancelled, Icons.Default.Close),
        )
    } else {
        listOf(
            TimelineStep(R.string.orders_timeline_offered, Icons.Outlined.LocalOffer),
            TimelineStep(R.string.orders_timeline_accepted, Icons.Outlined.ThumbUp),
            TimelineStep(R.string.orders_timeline_preparing, Icons.Outlined.Inventory2),
            TimelineStep(
                R.string.orders_timeline_ready,
                when {
                    fulfillmentMethod == FulfillmentMethod.PICKUP || status == OrderStatus.READY_FOR_PICKUP ->
                        Icons.Outlined.Storefront

                    fulfillmentMethod == FulfillmentMethod.DELIVERY ||
                            status == OrderStatus.READY_FOR_DELIVERY ||
                            status == OrderStatus.OUT_FOR_DELIVERY -> Icons.Outlined.LocalShipping

                    else -> Icons.Outlined.Inventory2
                },
            ),
            TimelineStep(R.string.orders_timeline_delivered, Icons.Outlined.TaskAlt),
        )
    }
    val currentStep = when (status) {
        OrderStatus.PENDING, OrderStatus.PENDING_PAYMENT -> 1
        OrderStatus.PREPARING -> 2
        OrderStatus.READY_FOR_PICKUP, OrderStatus.READY_FOR_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> 3
        OrderStatus.DELIVERED -> 4
        OrderStatus.CANCELLED -> 1
        OrderStatus.UNKNOWN -> null
    }
    val successColor = MaterialTheme.extendedColors.success
    val warningColor = MaterialTheme.extendedColors.warning
    val errorColor = MaterialTheme.colorScheme.error
    val futureColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
    val connectorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val layoutDirection = LocalLayoutDirection.current

    Box(Modifier.fillMaxWidth()) {
        Canvas(Modifier
            .fillMaxWidth()
            .height(28.dp)) {
            val centers = List(steps.size) { index ->
                val logicalCenter = size.width * (index + 0.5f) / steps.size
                if (layoutDirection == LayoutDirection.Ltr) logicalCenter else size.width - logicalCenter
            }
            for (index in 0 until centers.lastIndex) {
                val destinationIndex = index + 1
                val lineColor = when {
                    cancelled && destinationIndex == 1 -> errorColor
                    currentStep != null && destinationIndex <= currentStep -> successColor
                    currentStep != null && destinationIndex == currentStep + 1 -> warningColor
                    else -> connectorColor
                }
                drawLine(
                    color = lineColor,
                    start = androidx.compose.ui.geometry.Offset(centers[index], 14.dp.toPx()),
                    end = androidx.compose.ui.geometry.Offset(centers[index + 1], 14.dp.toPx()),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            steps.forEachIndexed { index, step ->
                val reached = currentStep != null && index <= currentStep
                val next = currentStep != null && index == currentStep + 1
                val cancelledStep = cancelled && index == 1
                val color = when {
                    cancelledStep -> MaterialTheme.colorScheme.error
                    reached -> successColor
                    next -> warningColor
                    else -> futureColor
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    cancelledStep -> MaterialTheme.colorScheme.errorContainer
                                    reached -> MaterialTheme.extendedColors.successContainer
                                    next -> MaterialTheme.extendedColors.warningContainer
                                    else -> MaterialTheme.colorScheme.surface
                                },
                            )
                            .border(2.dp, color, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (cancelledStep) Icons.Default.Close else step.icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    Text(
                        stringResource(step.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (reached || next) FontWeight.Bold else FontWeight.Medium,
                        color = color,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private data class TimelineStep(
    val labelRes: Int,
    val icon: ImageVector,
)

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
