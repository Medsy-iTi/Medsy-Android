package com.medsy.presentation.orders.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.components.OrderDetailsLineItemRow
import com.medsy.presentation.orders.details.components.OrderDetailsPharmacyCard
import com.medsy.presentation.orders.details.components.OrderDetailsPriceSummary
import com.medsy.presentation.orders.details.components.OrderDetailsReorderBar
import com.medsy.presentation.orders.details.components.OrderDetailsStatusHeader
import com.medsy.presentation.orders.details.components.OrderDetailsTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderDetailsRoot(
    orderId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPharmacyProfile: (String) -> Unit,
    onReorder: (String) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(orderId) {
        viewModel.init(orderId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                OrderDetailsUIEffect.NavigateBack -> onNavigateBack()
                is OrderDetailsUIEffect.NavigateToPharmacyProfile ->
                    onNavigateToPharmacyProfile(effect.pharmacyId)
                is OrderDetailsUIEffect.ReorderRequested -> onReorder(effect.orderId)
            }
        }
    }

    OrderDetailsScreen(
        orderId = orderId,
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun OrderDetailsScreen(
    orderId: String,
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            OrderDetailsTopBar(
                orderId = orderId,
                onBackClick = { onIntent(OrderDetailsUIIntent.BackClicked) },
            )
        },
    ) { paddingValues ->
        OrderDetailsContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun OrderDetailsContent(
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.errorMessageRes != null || state.order == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(state.errorMessageRes ?: R.string.order_details_error_load),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = { onIntent(OrderDetailsUIIntent.RetryClicked) }) {
                    Text(text = stringResource(R.string.product_details_error_retry))
                }
            }
        }
        return
    }

    val order = state.order

    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                OrderDetailsStatusHeader(
                    status = order.status,
                    dateLabel = order.dateLabel,
                    fulfillmentType = order.fulfillmentType,
                )
            }

            if (order.pharmacy != null) {
                item {
                    OrderDetailsPharmacyCard(
                        pharmacyName = order.pharmacy.name,
                        onClick = { onIntent(OrderDetailsUIIntent.PharmacyClicked) },
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.order_details_items_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            items(order.lineItems, key = { it.id }) { item ->
                OrderDetailsLineItemRow(item = item)
            }

            item {
                OrderDetailsPriceSummary(
                    itemsSubtotal = order.itemsSubtotal,
                    deliveryFee = order.deliveryFee,
                    finalTotal = order.finalTotal,
                )
            }
        }

        OrderDetailsReorderBar(
            onReorderClick = { onIntent(OrderDetailsUIIntent.ReorderClicked) },
        )
    }
}
