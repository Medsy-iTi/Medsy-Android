package com.medsy.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyButton
import com.medsy.domain.cart.model.CartItem
import com.medsy.presentation.R
import com.medsy.presentation.cart.CartState
import com.medsy.presentation.cart.CartUIIntent

@Composable
internal fun CartContent(
    state: CartState,
    onIntent: (CartUIIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CartTopBar(
            canClear = state.hasContent && !state.isClearing,
            onClear = { onIntent(CartUIIntent.ClearCartClicked) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.items.isEmpty()) {
                item { CartEmptyMessage() }
            } else {
                items(state.items, key = CartItem::id) { item ->
                    CartItemCard(
                        item = item,
                        isUpdating = item.id in state.updatingItemIds,
                        onIncrease = {
                            onIntent(CartUIIntent.IncreaseQuantityClicked(item.id))
                        },
                        onDecrease = {
                            onIntent(CartUIIntent.DecreaseQuantityClicked(item.id))
                        },
                        onRemove = { onIntent(CartUIIntent.RemoveItemClicked(item.id)) },
                    )
                }
            }
            item {
                CartPrescriptionSection(
                    image = state.draft.prescriptionImage,
                    onAdd = { onIntent(CartUIIntent.AddPrescriptionClicked) },
                    onRemove = { onIntent(CartUIIntent.RemovePrescriptionClicked) },
                )
            }
            item {
                CartNoteSection(
                    note = state.draft.pharmacistNote,
                    onClick = { onIntent(CartUIIntent.AddNoteClicked) },
                )
            }
            item { CartTotalSummary(state.totalPriceEgp) }
            item {
                MedsyButton(
                    onClick = {},
                ) {
                    Text(stringResource(R.string.cart_continue))
                }
            }
        }
    }
}

@Composable
private fun CartTopBar(
    canClear: Boolean,
    onClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.size(48.dp))
        Text(
            text = stringResource(R.string.cart_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onClear,
            enabled = canClear,
        ) {
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = stringResource(R.string.cart_clear_description),
            )
        }
    }
}
