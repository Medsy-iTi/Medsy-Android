package com.medsy.presentation.offers.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder
import com.medsy.designsystem.components.NullableProductImage
import com.medsy.designsystem.components.showError
import com.medsy.domain.offers.model.RequestResultItem
import com.medsy.domain.offers.model.ResultProduct
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.requests.model.MedicineRequestItem
import com.medsy.presentation.R
import com.medsy.presentation.common.util.PriceFormatter
import com.medsy.presentation.offers.OffersState
import com.medsy.presentation.offers.OffersUIEffect
import com.medsy.presentation.offers.OffersUIIntent
import com.medsy.presentation.offers.OffersViewModel
import com.medsy.presentation.offers.components.OfferTopAppBar

@Composable
fun OfferDetailsRoot(
    requestId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToOrderReview: (Long, List<SelectedOfferItem>) -> Unit,
    viewModel: OffersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(requestId) {
        viewModel.onIntent(OffersUIIntent.LoadOfferDetails(requestId))
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OffersUIEffect.NavigateBack -> onNavigateBack()
                is OffersUIEffect.NavigateToOrderReview -> onNavigateToOrderReview(
                    effect.masterOrderId,
                    effect.selectedItems,
                )
                is OffersUIEffect.ShowError -> snackbarHostState.showError(
                    ContextCompat.getString(context, effect.messageRes),
                )
            }
        }
    }

    OfferDetailsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun OfferDetailsScreen(
    state: OffersState,
    snackbarHostState: SnackbarHostState,
    onIntent: (OffersUIIntent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(snackbarHostState) },
        topBar = {
            OfferTopAppBar(
                title = stringResource(R.string.offers_details_title),
                onBackClick = { onIntent(OffersUIIntent.NavigateBack) },
                actions = {},
            )
        },
        bottomBar = {
            if (state.requestResult != null) {
                Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Box(Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        MedsyButton(
                            onClick = { onIntent(OffersUIIntent.ProceedToReview) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = state.selectedItems.isNotEmpty() && !state.isSubmitting,
                            isLoading = state.isSubmitting,
                        ) {
                            Text(
                                stringResource(
                                    if (state.selectionSubmitted && state.errorMessageRes != null) {
                                        R.string.offers_error_retry
                                    } else {
                                        R.string.offers_select_items
                                    }
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        },
    ) { padding ->
        when {
            state.isLoading && state.requestResult == null -> OfferDetailsShimmer(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
            )

            state.errorMessageRes != null && state.requestResult == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(state.errorMessageRes),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    MedsyButton(onClick = { onIntent(OffersUIIntent.Retry) }) {
                        Text(stringResource(R.string.offers_error_retry))
                    }
                }
            }

            else -> {
                val requestItems = state.request?.items.orEmpty()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.offers_single_offer_explanation),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            stringResource(R.string.offers_requested_medicines),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    items(requestItems, key = MedicineRequestItem::id) { requestedItem ->
                        val resultItem = state.requestResult?.items?.firstOrNull {
                            it.requestItemId == requestedItem.id
                        }
                        RequestedMedicineCard(
                            requestedItem = requestedItem,
                            resultItem = resultItem,
                            selectedProductId = state.selectedItems.firstOrNull {
                                it.requestItemId == requestedItem.id
                            }?.productId,
                            onSelect = { productId ->
                                onIntent(OffersUIIntent.SelectProduct(requestedItem.id, productId))
                            },
                        )
                    }
                    item { Spacer(Modifier.height(76.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RequestedMedicineCard(
    requestedItem: MedicineRequestItem,
    resultItem: RequestResultItem?,
    selectedProductId: Long?,
    onSelect: (Long?) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NullableProductImage(
                    imageUrl = requestedItem.imageUrl,
                    contentDescription = requestedItem.productName,
                    modifier = Modifier.size(72.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        requestedItem.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    requestedItem.strength?.takeIf(String::isNotBlank)?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        stringResource(R.string.offers_quantity_format, requestedItem.quantity),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            val exactProductId = resultItem?.productId ?: requestedItem.productId
            if (resultItem?.isAvailable == true && exactProductId != null && !resultItem.isAlternative) {
                ProductChoice(
                    product = resultItem.product,
                    fallbackName = requestedItem.productName,
                    productId = exactProductId,
                    price = resultItem.unitPrice.takeIf { it > 0.0 } ?: requestedItem.unitPrice,
                    label = stringResource(R.string.offers_requested_product_found),
                    selected = selectedProductId == exactProductId,
                    onSelect = onSelect,
                )
            }

            resultItem?.alternatives.orEmpty().forEach { alternative ->
                ProductChoice(
                    product = alternative,
                    fallbackName = alternative.productName,
                    productId = alternative.id,
                    price = alternative.price,
                    label = stringResource(R.string.offers_alternative_provided),
                    selected = selectedProductId == alternative.id,
                    onSelect = onSelect,
                )
            }

            if (resultItem?.isAlternative == true && resultItem.product != null &&
                resultItem.alternatives.none { it.id == resultItem.product!!.id }
            ) {
                val product = resultItem.product!!
                ProductChoice(
                    product = product,
                    fallbackName = product.productName,
                    productId = product.id,
                    price = resultItem.unitPrice.takeIf { it > 0 } ?: product.price,
                    label = stringResource(R.string.offers_alternative_provided),
                    selected = selectedProductId == product.id,
                    onSelect = onSelect,
                )
            }

            if (resultItem == null ||
                (!resultItem.isAvailable && resultItem.alternatives.isEmpty() && resultItem.product == null)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.offers_not_available),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            resultItem?.pharmacy?.let { pharmacy ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.offers_supplied_by, pharmacy.name),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ProductChoice(
    product: ResultProduct?,
    fallbackName: String,
    productId: Long,
    price: Double,
    label: String,
    selected: Boolean,
    onSelect: (Long?) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable {
                onSelect(if (selected) null else productId)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NullableProductImage(
                imageUrl = product?.imageUrl,
                contentDescription = product?.name ?: fallbackName,
                modifier = Modifier.size(52.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    product?.name ?: fallbackName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    stringResource(
                        R.string.search_price_egp,
                        PriceFormatter.formatPrice(price, LocalLocale.current.platformLocale),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            RadioButton(
                selected = selected,
                onClick = { onSelect(if (selected) null else productId) },
            )
        }
    }
}

@Composable
private fun OfferDetailsShimmer(modifier: Modifier = Modifier) {
    MedsyShimmer(modifier) {
        Column(Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            MedsyShimmerPlaceholder(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(20.dp))
            MedsyShimmerPlaceholder(Modifier
                .fillMaxWidth(0.45f)
                .height(24.dp))
            Spacer(Modifier.height(12.dp))
            repeat(3) {
                MedsyShimmerPlaceholder(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
