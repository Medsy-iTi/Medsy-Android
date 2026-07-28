package com.medsy.presentation.orders.details

import OrderNoteSection
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.components.OrderDetailsLineItemRow
import com.medsy.presentation.orders.details.components.OrderDetailsPharmacyCard
import com.medsy.presentation.orders.details.components.OrderDetailsPriceSummary
import com.medsy.presentation.orders.details.components.OrderDetailsReorderBar
import com.medsy.presentation.orders.details.components.OrderDetailsShimmer
import com.medsy.presentation.orders.details.components.OrderDetailsStatusHeader
import com.medsy.presentation.orders.details.components.OrderDetailsTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderDetailsRoot(
    orderId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPharmacyProfile: (Long) -> Unit,
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
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(OrderDetailsUIIntent.Refresh) },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OrderDetailsContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun OrderDetailsContent(
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        OrderDetailsShimmer(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
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
                        pharmacy = order.pharmacy,
                        onClick = { onIntent(OrderDetailsUIIntent.PharmacyClicked) },
                    )
                }
            }

            if (!order.prescriptionImage.isNullOrBlank()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.order_details_prescription_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            ),
                        ) {
                            AsyncImage(
                                model = order.prescriptionImage,
                                contentDescription = stringResource(R.string.order_details_prescription_title),
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.order_details_items_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        order.lineItems.forEachIndexed { index, lineItem ->
                            OrderDetailsLineItemRow(item = lineItem)
                            if (index < order.lineItems.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                OrderNoteSection(
                    titleRes = R.string.order_details_customer_note_title,
                    note = order.customerNote
                )
            }

            item {
                OrderNoteSection(
                    titleRes = R.string.order_details_pharmacy_note_title,
                    note = order.pharmacyNote
                )
            }

            item {
                OrderDetailsPriceSummary(
                    itemsSubtotal = order.itemsSubtotal.toInt(),
                    deliveryFee = order.deliveryFee?.toInt(),
                    finalTotal = order.finalTotal.toInt(),
                )
            }
        }

        OrderDetailsReorderBar(
            onReorderClick = { onIntent(OrderDetailsUIIntent.ReorderClicked) },
        )
    }
}
