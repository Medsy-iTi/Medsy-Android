package com.medsy.presentation.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.medsy.presentation.orders.model.OrderStatus

@Composable
fun OrderStatusText(
    status: OrderStatus,
    pharmacyName: String?,
    modifier: Modifier = Modifier,
) {
    val (statusLabelRes, statusColor, statusBgColor) = when (status) {
        OrderStatus.Confirmed -> Triple(
            R.string.orders_status_confirmed,
            MaterialTheme.extendedColors.onSuccessContainer,
            MaterialTheme.extendedColors.successContainer
        )
        OrderStatus.Delivered -> Triple(
            R.string.orders_status_delivered,
            MaterialTheme.extendedColors.onSuccessContainer,
            MaterialTheme.extendedColors.successContainer
        )
        OrderStatus.Cancelled -> Triple(
            R.string.orders_status_cancelled,
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.errorContainer
        )
    }

    val subtitle = if (status == OrderStatus.Cancelled) {
        stringResource(R.string.orders_cancelled_note)
    } else if (!pharmacyName.isNullOrBlank()) {
        stringResource(R.string.orders_from_pharmacy_format, pharmacyName)
    } else {
        null
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusBgColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(statusLabelRes),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
            }
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
