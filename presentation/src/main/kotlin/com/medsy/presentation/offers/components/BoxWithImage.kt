package com.medsy.presentation.offers.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.NullableProductImage

@Composable
fun BoxWithImage(imageUrl: String?) {
    NullableProductImage(
        imageUrl = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentScale = ContentScale.Fit,
    )
}
