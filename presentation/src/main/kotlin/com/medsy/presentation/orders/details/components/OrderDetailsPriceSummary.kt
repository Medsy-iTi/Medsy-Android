package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.common.util.PriceFormatter

@Composable
fun OrderDetailsPriceSummary(itemsSubtotal: Double, deliveryFee: Double, finalTotal: Double) {
    val locale = LocalConfiguration.current.locales[0]
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.order_details_summary_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            SummaryRow(R.string.order_details_items_subtotal, PriceFormatter.formatPrice(itemsSubtotal, locale))
            SummaryRow(R.string.order_details_delivery_fee, PriceFormatter.formatPrice(deliveryFee, locale))
            HorizontalDivider()
            SummaryRow(R.string.order_details_final_total, PriceFormatter.formatPrice(finalTotal, locale), true)
        }
    }
}

@Composable
private fun SummaryRow(labelRes: Int, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(stringResource(labelRes), fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(
            stringResource(R.string.search_price_egp, value),
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}
