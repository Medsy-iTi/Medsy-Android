package com.medsy.presentation.productdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

/**
 * Back + favorite row. Uses Icons.AutoMirrored so the arrow flips automatically
 * under the Arabic (RTL) locale.
 */
@Composable
fun ProductTopBar(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = stringResource(R.string.search_add_to_cart_desc),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
