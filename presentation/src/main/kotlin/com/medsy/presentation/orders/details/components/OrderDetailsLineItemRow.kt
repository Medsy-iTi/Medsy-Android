package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.model.OrderLineItem


@Composable
fun OrderDetailsLineItemRow(
    item: OrderLineItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = stringResource(R.string.orders_product_image_desc),
                modifier = Modifier.size(56.dp),
                placeholder = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
                error = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
                fallback = painterResource(com.medsy.designsystem.R.drawable.ic_logo_transparent),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp),
        ) {
            Text(
                text = item.medicineName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (item.alternativeToMedicineName != null) {
                Text(
                    text = stringResource(
                        R.string.order_details_alternative_to_format,
                        item.alternativeToMedicineName,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.extendedColors.info,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Text(
                text = stringResource(
                    R.string.order_details_qty_x_price_format,
                    item.quantity,
                    item.unitPrice.toInt(),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Text(
            text = stringResource(R.string.search_price_egp, item.lineTotal.toInt()),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
