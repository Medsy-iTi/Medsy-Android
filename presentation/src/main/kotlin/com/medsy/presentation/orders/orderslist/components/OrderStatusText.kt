package com.medsy.presentation.orders.orderslist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.presentation.R

@Composable
fun OrderStatusText(
    status: OrderStatus,
    pharmacyName: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(status.labelRes()),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (status == OrderStatus.CANCELLED) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (status == OrderStatus.CANCELLED) MaterialTheme.colorScheme.onErrorContainer
            else MaterialTheme.colorScheme.onPrimaryContainer,
        )
        if (!pharmacyName.isNullOrBlank()) {
            Text(
                stringResource(R.string.orders_from_pharmacy_format, pharmacyName),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
