package com.medsy.presentation.productdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.ErrorRed
import com.medsy.presentation.R

@Composable
fun ProductDetailsList(
    manufacturer: String,
    category: String,
    usage: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        ProductDetailRow(
            icon = Icons.Filled.Science,
            label = stringResource(R.string.product_details_label_manufacturer),
            value = manufacturer,
        )
        ProductDetailRow(
            icon = Icons.Filled.Category,
            label = stringResource(R.string.product_details_label_category),
            value = category,
        )
        ProductDetailRow(
            icon = Icons.Filled.MedicalServices,
            label = stringResource(R.string.product_details_label_usage_method),
            value = usage,
            showDivider = false,
        )
    }
}
