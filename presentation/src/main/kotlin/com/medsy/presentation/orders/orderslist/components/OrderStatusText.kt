package com.medsy.presentation.orders.orderslist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
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
import com.medsy.presentation.orders.orderslist.model.OrderStatus

@Composable
fun OrderStatusText(
    status: OrderStatus,
    pharmacyName: String?,
    modifier: Modifier = Modifier,
) {
    val (statusLabelRes, statusColor, statusBgColor, statusIcon) = when (status) {
        OrderStatus.Confirmed -> Quadruple(
            R.string.orders_status_confirmed,
            MaterialTheme.extendedColors.onSuccessContainer,
            MaterialTheme.extendedColors.successContainer,
            Icons.Outlined.Schedule
        )
        OrderStatus.Delivered -> Quadruple(
            R.string.orders_status_delivered,
            MaterialTheme.extendedColors.onSuccessContainer,
            MaterialTheme.extendedColors.successContainer,
            Icons.Outlined.CheckCircle
        )
        OrderStatus.Cancelled -> Quadruple(
            R.string.orders_status_cancelled,
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.errorContainer,
            Icons.Outlined.Cancel
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(statusLabelRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                    )
                }
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

private data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
