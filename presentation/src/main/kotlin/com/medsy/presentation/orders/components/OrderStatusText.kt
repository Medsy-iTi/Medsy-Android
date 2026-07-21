package com.medsy.presentation.orders.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.model.OrderStatus

@Composable
fun OrderStatusText(
    status: OrderStatus,
    pharmacyName: String?,
    modifier: Modifier = Modifier,
) {
    val (statusLabelRes, statusColor) = when (status) {
        OrderStatus.Confirmed -> R.string.orders_status_confirmed to MaterialTheme.extendedColors.success
        OrderStatus.Delivered -> R.string.orders_status_delivered to MaterialTheme.extendedColors.success
        OrderStatus.Cancelled -> R.string.orders_status_cancelled to MaterialTheme.colorScheme.error
    }

    val subtitle = if (status == OrderStatus.Cancelled) {
        stringResource(R.string.orders_cancelled_note)
    } else {
        stringResource(R.string.orders_from_pharmacy_format, pharmacyName.orEmpty())
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(statusLabelRes),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = statusColor,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
