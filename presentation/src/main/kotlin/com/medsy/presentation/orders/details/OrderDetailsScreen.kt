package com.medsy.presentation.orders.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.components.OrderDetailsLineItemRow
import com.medsy.presentation.orders.details.components.OrderDetailsPharmacyCard
import com.medsy.presentation.orders.details.components.OrderDetailsPriceSummary
import com.medsy.presentation.orders.details.components.OrderDetailsReorderBar
import com.medsy.presentation.orders.details.components.OrderDetailsShimmer
import com.medsy.presentation.orders.details.components.OrderDetailsStatusHeader
import com.medsy.presentation.orders.details.components.OrderDetailsTopBar

@Composable
fun OrderDetailsRoot(
    orderId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPharmacyProfile: (Long) -> Unit,
    onReorder: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(orderId) { viewModel.init(orderId) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderDetailsUIEffect.NavigateBack -> onNavigateBack()
                is OrderDetailsUIEffect.NavigateToPharmacyProfile -> onNavigateToPharmacyProfile(
                    effect.pharmacyId
                )

                OrderDetailsUIEffect.ReorderRequested -> onReorder()
                is OrderDetailsUIEffect.NavigateToProductDetails -> onNavigateToProductDetails(
                    effect.productId
                )

                is OrderDetailsUIEffect.ShowErrorSnackbar -> snackbarHostState.showError(
                    ContextCompat.getString(context, effect.messageRes)
                )
            }
        }
    }

    OrderDetailsScreen(orderId, state, snackbarHostState, viewModel::onIntent)
}

@Composable
fun OrderDetailsScreen(
    orderId: String,
    state: OrderDetailsUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(snackbarHostState) },
        topBar = {
            OrderDetailsTopBar(
                orderId,
                onBackClick = {
                    if (!state.isReordering) onIntent(OrderDetailsUIIntent.BackClicked)
                }
            )
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { if (!state.isReordering) onIntent(OrderDetailsUIIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OrderDetailsContent(state, onIntent)
        }
    }
}

@Composable
private fun OrderDetailsContent(
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
) {
    if (state.isLoading) {
        OrderDetailsShimmer(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
        return
    }
    val order = state.order
    if (state.errorMessageRes != null || order == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(state.errorMessageRes ?: R.string.order_details_error_load),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = { onIntent(OrderDetailsUIIntent.RetryClicked) }) {
                    Text(stringResource(R.string.product_details_error_retry))
                }
            }
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { OrderDetailsStatusHeader(order.orderStatus, order.fulfillmentMethod) }

            if (order.pharmacyAllocations.count { it.pharmacyName.isNotBlank() } > 1) {
                item {
                    Text(
                        stringResource(R.string.offers_pharmacies_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            order.pharmacyAllocations.filter { it.pharmacyName.isNotBlank() }.forEach { pharmacy ->
                item(key = "pharmacy-${pharmacy.subOrderId}") {
                    OrderDetailsPharmacyCard(
                        pharmacy,
                        onClick = {
                            onIntent(OrderDetailsUIIntent.PharmacyClicked(pharmacy.pharmacyId))
                        }
                    )
                }
            }

            item {
                Text(
                    stringResource(R.string.order_details_items_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    ),
                ) {
                    if (order.items.isEmpty()) {
                        Text(
                            stringResource(R.string.order_details_items_unavailable),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Column(Modifier.padding(16.dp)) {
                            val allocatedItems = order.pharmacyAllocations.flatMap { allocation ->
                                allocation.items.map { allocation to it }
                            }
                            allocatedItems.forEachIndexed { index, (allocation, item) ->
                                OrderDetailsLineItemRow(
                                    item,
                                    allocation.pharmacyName,
                                    onClick = {
                                        onIntent(OrderDetailsUIIntent.LineItemClicked(item.productId.toString()))
                                    }
                                )
                                if (index < allocatedItems.lastIndex) {
                                    HorizontalDivider(
                                        Modifier.padding(vertical = 12.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                OrderDetailsPriceSummary(
                    itemsSubtotal = order.itemSubtotal,
                    deliveryFee = order.displayedDeliveryFee,
                    finalTotal = order.displayedTotal,
                )
            }
        }

        if (order.orderStatus == OrderStatus.DELIVERED && order.items.isNotEmpty()) {
            OrderDetailsReorderBar(
                isLoading = state.isReordering,
                enabled = !state.isReordering,
                onReorderClick = { onIntent(OrderDetailsUIIntent.ReorderClicked) },
            )
        }
    }
}
