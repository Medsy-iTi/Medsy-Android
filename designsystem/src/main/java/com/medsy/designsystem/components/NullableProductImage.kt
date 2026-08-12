package com.medsy.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.medsy.designsystem.R

@Composable
fun NullableProductImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val logo = painterResource(R.drawable.ic_logo_transparent)

    AsyncImage(
        model = imageUrl?.takeIf(String::isNotBlank),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = logo,
        error = logo,
        fallback = logo,
    )
}
