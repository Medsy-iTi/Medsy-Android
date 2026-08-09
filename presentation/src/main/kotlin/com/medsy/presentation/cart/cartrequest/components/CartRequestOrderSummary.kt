package com.medsy.presentation.cart.cartrequest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.cart.cartrequest.CartRequestState
import com.medsy.presentation.cart.components.cartPriceText

@Composable
internal fun CartRequestOrderSummary(
    state: CartRequestState,
    onRetry: () -> Unit,
) {
    CartRequestSection(
        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
        title = stringResource(R.string.cart_request_order_summary),
    ) {
        if (state.isCartLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp)
                    .size(28.dp),
                strokeWidth = 2.dp,
            )
        } else if (state.cartErrorMessageRes != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(state.cartErrorMessageRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                TextButton(onClick = onRetry) {
                    Text(text = stringResource(R.string.cart_request_retry))
                }
            }
        } else {
            val medicineCount = state.items.sumOf { it.quantity }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = pluralStringResource(
                        R.plurals.cart_request_item_count,
                        medicineCount,
                        medicineCount,
                    ),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = cartPriceText(state.totalPriceEgp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (state.draft.prescriptionImage != null) {
                StatusPill(
                    label = stringResource(R.string.cart_request_prescription_attached),
                )
            }
            if (!state.hasProducts) {
                Text(
                    text = stringResource(R.string.cart_request_empty_request),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
