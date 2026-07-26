package com.medsy.presentation.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.presentation.R
import com.medsy.presentation.orders.model.OrderProductThumbnail

@Composable
fun OrderProductThumbnails(
    thumbnails: List<OrderProductThumbnail>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy((-16).dp)
    ) {
        val visibleThumbnails = thumbnails.take(3)

        visibleThumbnails.forEach { thumbnail ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                    ),
            ) {
                if (thumbnail.imageUrl != null) {
                    AsyncImage(
                        model = thumbnail.imageUrl,
                        contentDescription = stringResource(R.string.orders_product_image_desc),
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
    }
}