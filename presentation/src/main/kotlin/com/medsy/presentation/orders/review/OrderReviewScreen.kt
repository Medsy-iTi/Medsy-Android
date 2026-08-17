package com.medsy.presentation.orders.review

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.NullableProductImage
import com.medsy.designsystem.components.showError
import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.orders.model.FulfillmentMethod
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.presentation.R
import com.medsy.presentation.common.util.PriceFormatter
import com.medsy.presentation.offers.components.OfferTopAppBar
import java.util.Locale

@Composable
fun OrderReviewRoot(
    requestId: Long,
    selectedItems: List<SelectedOfferItem>,
    masterOrderId: Long,
    onNavigateBack: () -> Unit,
    onStartCardPayment: (Long) -> Unit,
    onNavigateToOrderDetails: (Long) -> Unit,
    onNavigateToPharmacyProfile: (Long) -> Unit,
    viewModel: OrderReviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(requestId, selectedItems, masterOrderId) {
        viewModel.onIntent(OrderReviewUIIntent.Load(requestId, masterOrderId, selectedItems))
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onIntent(OrderReviewUIIntent.Refresh)
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderReviewUIEffect.NavigateBack -> onNavigateBack()
                is OrderReviewUIEffect.StartCardPayment ->
                    onStartCardPayment(effect.orderId)

                is OrderReviewUIEffect.NavigateToOrderDetails -> onNavigateToOrderDetails(effect.masterOrderId)
                is OrderReviewUIEffect.NavigateToPharmacyProfile -> onNavigateToPharmacyProfile(
                    effect.pharmacyId
                )

                is OrderReviewUIEffect.ShowError -> snackbarHostState.showError(
                    ContextCompat.getString(context, effect.messageRes),
                )
            }
        }
    }

    OrderReviewScreen(state, snackbarHostState, viewModel::onIntent)
}

@Composable
fun OrderReviewScreen(
    state: OrderReviewState,
    snackbarHostState: SnackbarHostState,
    onIntent: (OrderReviewUIIntent) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                OfferTopAppBar(
                    title = stringResource(R.string.offers_order_review_title),
                    onBackClick = { onIntent(OrderReviewUIIntent.NavigateBack) },
                    actions = {},
                )
            },
            bottomBar = {
                if (state.order != null && state.nextAction != OrderNextAction.VIEW_DETAILS) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        MedsyButton(
                            onClick = {
                                when {
                                    state.nextAction == OrderNextAction.PAY_CARD -> {
                                        onIntent(OrderReviewUIIntent.PayClicked)
                                    }

                                    state.fulfillmentConfirmed -> {
                                        onIntent(OrderReviewUIIntent.Retry)
                                    }

                                    else -> {
                                        onIntent(OrderReviewUIIntent.ConfirmFulfillment)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !state.isConfirming && (
                                    state.nextAction == OrderNextAction.PAY_CARD ||
                                            state.selectedFulfillment != null
                                    ),
                            isLoading = state.isConfirming,
                            snackbarHostState = snackbarHostState,
                        ) {
                            Text(
                                stringResource(
                                    when {
                                        state.nextAction == OrderNextAction.CHOOSE_FULFILLMENT ->
                                            R.string.offers_confirm_order
                                        state.isRetryPayment -> R.string.order_details_try_payment_again
                                        else -> R.string.order_details_pay_now
                                    }
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            },
        ) { padding ->
            when {
                state.isLoading -> OrderReviewShimmer(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                )

                state.errorMessageRes != null && state.order == null -> Box(
                    Modifier
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
                        MedsyButton(onClick = { onIntent(OrderReviewUIIntent.Retry) }) {
                            Text(stringResource(R.string.offers_error_retry))
                        }
                    }
                }

                else -> PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onIntent(OrderReviewUIIntent.Refresh) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    ReviewContent(state, PaddingValues(), onIntent)
                }
            }
        }
        MedsySnackbarHost(snackbarHostState)
    }
}

@Composable
private fun ReviewContent(
    state: OrderReviewState,
    padding: PaddingValues,
    onIntent: (OrderReviewUIIntent) -> Unit,
) {
    val selectedRows = state.selectedItems.mapNotNull { selection ->
        val requestItem = state.request?.items?.firstOrNull { it.id == selection.requestItemId }
        val resultItem =
            state.requestResult?.items?.firstOrNull { it.requestItemId == selection.requestItemId }
        val selectedProduct = buildList {
            resultItem?.product?.let(::add)
            addAll(resultItem?.alternatives.orEmpty())
        }.firstOrNull { it.id == selection.productId }
        requestItem?.let { Triple(selection, it, selectedProduct) }
    }
    val locale = LocalLocale.current.platformLocale

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { SectionTitle(stringResource(R.string.offers_selected_medicines)) }

        if (selectedRows.isEmpty()) {
            val orderItems = state.order?.items.orEmpty()
            if (orderItems.isNotEmpty()) {
                items(orderItems, key = { it.id }) { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NullableProductImage(
                            imageUrl = item.product?.imageUrl,
                            contentDescription = item.product?.name,
                            modifier = Modifier.size(64.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                item.product?.name
                                    ?: stringResource(R.string.offers_unknown_product),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                stringResource(R.string.offers_quantity_format, item.quantity),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            stringResource(
                                R.string.search_price_egp,
                                PriceFormatter.formatPrice(item.totalPrice, locale),
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        stringResource(R.string.order_details_items_unavailable),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        items(selectedRows, key = { it.first.requestItemId }) { (_, requestItem, selectedProduct) ->
            val selectedProductId = selectedProduct?.id ?: requestItem.productId
            val pharmacyName = state.order?.pharmacyAllocations
                ?.firstOrNull { allocation -> allocation.items.any { it.productId == selectedProductId } }
                ?.pharmacyName
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NullableProductImage(
                    imageUrl = selectedProduct?.imageUrl ?: requestItem.imageUrl,
                    contentDescription = selectedProduct?.name ?: requestItem.productName,
                    modifier = Modifier.size(64.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        selectedProduct?.name ?: requestItem.productName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        stringResource(R.string.offers_quantity_format, requestItem.quantity),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    pharmacyName?.takeIf(String::isNotBlank)?.let {
                        Text(
                            stringResource(R.string.offers_supplied_by, it),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Text(
                    stringResource(
                        R.string.search_price_egp,
                        PriceFormatter.formatPrice(
                            selectedProduct?.price ?: requestItem.unitPrice,
                            locale,
                        ),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item { PharmaciesCard(state, onIntent) }
        item { PaymentMethodCard(state) }
        if (state.nextAction == OrderNextAction.PAY_CARD) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.order_details_payment_required_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.order_details_payment_required_message),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
        if (state.nextAction == OrderNextAction.CHOOSE_FULFILLMENT) item {
            SectionTitle(stringResource(R.string.offers_fulfillment_title))
            Spacer(Modifier.height(8.dp))
            FulfillmentChoice(
                title = stringResource(R.string.order_details_fulfillment_delivery),
                subtitle = stringResource(
                    R.string.offers_fulfillment_total,
                    PriceFormatter.formatPrice(state.order?.totalPrice ?: 0.0, locale),
                ),
                selected = state.selectedFulfillment == FulfillmentMethod.DELIVERY,
                icon = Icons.Default.LocalShipping,
                onClick = { onIntent(OrderReviewUIIntent.FulfillmentChanged(FulfillmentMethod.DELIVERY)) },
            )
            Spacer(Modifier.height(10.dp))
            FulfillmentChoice(
                title = stringResource(R.string.order_details_fulfillment_pickup),
                subtitle = stringResource(
                    R.string.offers_fulfillment_total,
                    PriceFormatter.formatPrice(state.pickupTotal, locale),
                ),
                selected = state.selectedFulfillment == FulfillmentMethod.PICKUP,
                icon = Icons.Default.Storefront,
                onClick = { onIntent(OrderReviewUIIntent.FulfillmentChanged(FulfillmentMethod.PICKUP)) },
            )
        }

        state.request?.deliveryAddress
            ?.takeIf { it.isNotBlank() && state.selectedFulfillment == FulfillmentMethod.DELIVERY }
            ?.let { address -> item { DeliveryAddressCard(address) } }

        item {
            PriceSummaryCard(
                medicinesPrice = state.order?.itemSubtotal ?: 0.0,
                deliveryFee = state.displayedDeliveryFee,
                total = state.displayedTotal,
                locale = locale,
            )
        }
        item { Spacer(Modifier.height(76.dp)) }
    }
}

@Composable
private fun PharmaciesCard(state: OrderReviewState, onIntent: (OrderReviewUIIntent) -> Unit) {
    val pharmacies = state.order?.pharmacyAllocations.orEmpty()
        .filter { it.pharmacyName.isNotBlank() }
        .distinctBy { it.pharmacyId }
    Column {
        SectionTitle(stringResource(R.string.offers_pharmacies_title))
        Spacer(Modifier.height(8.dp))
        if (pharmacies.isEmpty()) {
            Text(
                stringResource(R.string.offers_pharmacies_unavailable),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            pharmacies.forEach { pharmacy ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = pharmacy.pharmacyId > 0L) {
                            onIntent(OrderReviewUIIntent.PharmacyClicked(pharmacy.pharmacyId))
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        pharmacy.pharmacyName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.pharmacy_profile_title),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(state: OrderReviewState) {
    val paymentLabel = when (
        state.order?.paymentMethod?.takeUnless { it == PaymentMethod.UNKNOWN }
            ?: state.request?.paymentMethod
    ) {
        PaymentMethod.CASH -> R.string.offers_payment_cash
        PaymentMethod.CARD -> R.string.offers_payment_card
        else -> R.string.offers_payment_unknown
    }
    Column {
        SectionTitle(stringResource(R.string.offers_payment_method))
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(paymentLabel),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FulfillmentChoice(
    title: String,
    subtitle: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@Composable
private fun DeliveryAddressCard(address: String) {
    Column {
        SectionTitle(stringResource(R.string.offers_delivery_address))
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    address,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun PriceSummaryCard(
    medicinesPrice: Double,
    deliveryFee: Double,
    total: Double,
    locale: Locale,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(stringResource(R.string.order_details_summary_title))
        PriceLine(R.string.offers_total_medicines_price, medicinesPrice, locale)
        PriceLine(R.string.offers_delivery_fee, deliveryFee, locale)
        HorizontalDivider()
        PriceLine(R.string.offers_total, total, locale, bold = true)
    }
}

@Composable
private fun PriceLine(labelRes: Int, value: Double, locale: Locale, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            stringResource(labelRes),
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            stringResource(R.string.search_price_egp, PriceFormatter.formatPrice(value, locale)),
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun OrderReviewShimmer(modifier: Modifier = Modifier) {
    MedsyShimmer(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MedsyShimmerPlaceholder(
                Modifier
                    .fillMaxWidth(0.45f)
                    .height(24.dp)
            )
            Spacer(Modifier.height(12.dp))
            repeat(3) {
                MedsyShimmerPlaceholder(
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(12.dp))
            MedsyShimmerPlaceholder(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                RoundedCornerShape(16.dp)
            )
        }
    }
}
