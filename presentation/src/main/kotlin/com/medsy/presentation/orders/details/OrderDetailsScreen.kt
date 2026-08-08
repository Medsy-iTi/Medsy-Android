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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
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
    onReorder: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(orderId) {
        viewModel.init(orderId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                OrderDetailsUIEffect.NavigateBack -> onNavigateBack()
                is OrderDetailsUIEffect.NavigateToPharmacyProfile ->
                    onNavigateToPharmacyProfile(effect.pharmacyId)

                is OrderDetailsUIEffect.ReorderRequested -> {
                    onReorder()
                }

                is OrderDetailsUIEffect.NavigateToProductDetails ->
                    onNavigateToProductDetails(effect.productId)

                is OrderDetailsUIEffect.ShowErrorSnackbar -> {
                    snackbarHostState.showError(
                        message = ContextCompat.getString(context, effect.messageRes)
                    )
                }
            }
        }
    }

    OrderDetailsScreen(
        orderId = orderId,
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun OrderDetailsScreen(
    orderId: String,
    state: OrderDetailsUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
        topBar = {
            OrderDetailsTopBar(
                orderId = orderId,
                onBackClick = {
                    if (!state.isReordering) {
                        onIntent(OrderDetailsUIIntent.BackClicked)
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    if (!state.isReordering) {
                        onIntent(OrderDetailsUIIntent.Refresh)
                    }
                },
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

            if (state.isReordering) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {}
                )
            }
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
                    text = stringResource(
                        state.errorMessageRes ?: R.string.order_details_error_load
                    ),
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
                            OrderDetailsLineItemRow(
                                item = lineItem,
                                onClick = { onIntent(OrderDetailsUIIntent.LineItemClicked(lineItem.productId.toString())) }
                            )
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
                OrderDetailsPriceSummary(
                    itemsSubtotal = order.itemsSubtotal.toInt(),
                    deliveryFee = order.deliveryFee?.toInt(),
                    finalTotal = order.finalTotal.toInt(),
                )
            }
        }

        OrderDetailsReorderBar(
            isLoading = state.isReordering,
            enabled = !state.isReordering,
            onReorderClick = { onIntent(OrderDetailsUIIntent.ReorderClicked) },
        )
    }
}
