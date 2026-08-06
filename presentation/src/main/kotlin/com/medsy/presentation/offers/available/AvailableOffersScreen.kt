package com.medsy.presentation.offers.available

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.showError
import com.medsy.designsystem.util.isNetworkAvailable
import kotlinx.coroutines.launch
import com.medsy.presentation.R
import com.medsy.presentation.offers.OffersState
import com.medsy.presentation.offers.OffersUIEffect
import com.medsy.presentation.offers.OffersUIIntent
import com.medsy.presentation.offers.OffersViewModel
import com.medsy.presentation.offers.components.OfferTopAppBar
import com.medsy.domain.offers.model.RequestResultItem

@Composable
fun AvailableOffersRoot(
    requestId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToOfferDetails: (String) -> Unit, // Might not need this anymore
    onNavigateToOrderReview: () -> Unit,
    viewModel: OffersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(requestId) {
        viewModel.onIntent(OffersUIIntent.LoadOffers(requestId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OffersUIEffect.NavigateBack -> onNavigateBack()
                is OffersUIEffect.NavigateToOrderReview -> onNavigateToOrderReview()
                else -> Unit
            }
        }
    }

    AvailableOffersScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun AvailableOffersScreen(
    state: OffersState,
    onIntent: (OffersUIIntent) -> Unit
) {
    val snackbarHostState = androidx.compose.runtime.remember { androidx.compose.material3.SnackbarHostState() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                OfferTopAppBar(
                    title = stringResource(R.string.offers_available_title),
                    onBackClick = { onIntent(OffersUIIntent.NavigateBack) },
                    actions = {}
                )
            },
            bottomBar = {
                if (state.requestResult != null && state.selectedItemIds.isNotEmpty()) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Button(
                            onClick = { onIntent(OffersUIIntent.ProceedToReview) },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(text = stringResource(R.string.offers_review_order))
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading && state.requestResult == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (state.requestResult == null || state.requestResult.items.isEmpty()) {
                    Text(
                        text = stringResource(R.string.offers_available_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val items = state.requestResult.items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Total estimated price: EGP ${state.requestResult.totalPrice}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        
                        items(items, key = { it.requestItemId }) { item ->
                            RequestResultItemCard(
                                item = item,
                                isSelected = state.selectedItemIds.contains(item.requestItemId),
                                onToggle = { onIntent(OffersUIIntent.ToggleItemSelection(item.requestItemId)) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
        com.medsy.designsystem.components.MedsySnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun RequestResultItemCard(
    item: RequestResultItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product?.productName ?: "Unknown Product",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (item.isAlternative) {
                    Text(
                        text = "Alternative provided",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "EGP ${item.unitPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!item.isAvailable) {
                    Text(
                        text = "Out of stock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (item.isAvailable) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle() }
                )
            }
        }
    }
}
