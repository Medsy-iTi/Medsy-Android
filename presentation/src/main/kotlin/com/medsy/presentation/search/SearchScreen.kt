package com.medsy.presentation.search

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showMessage
import com.medsy.presentation.R
import kotlinx.coroutines.flow.collectLatest
import com.medsy.presentation.search.components.ProductResultCard
import com.medsy.presentation.search.components.SearchCategoryBottomSheet
import com.medsy.presentation.search.components.SearchEmptyState
import com.medsy.presentation.search.components.SearchFilterChips
import com.medsy.presentation.search.components.SearchInputBar
import com.medsy.presentation.search.components.SearchResultsHeader
import com.medsy.presentation.search.components.SearchSelectionBottomSheet
import com.medsy.presentation.search.components.SearchTopBar

@Composable
fun SearchRoot(
    initialQuery: String? = null,
    onBack: () -> Unit,
    onNext: (String) -> Unit,
    onProductSelected: ((String) -> Unit)? = null,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(initialQuery) {
        if (initialQuery != null) {
            viewModel.onIntent(SearchUIIntent.QueryChanged(initialQuery))
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SearchUIEffect.NavigateBack -> onBack()
                is SearchUIEffect.NavigateToProductDetails -> {
                    if (onProductSelected != null) {
                        onProductSelected(effect.productId)
                    } else {
                        onNext(effect.productId)
                    }
                }

                is SearchUIEffect.ShowMessage ->
                    snackbarHostState.showMessage(
                        context = context,
                        messageRes = effect.messageRes,
                        isSuccess = effect.isSuccess,
                        args = effect.args,
                    )
            }
        }
    }

    Scaffold(
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        SearchScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 4
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onIntent(SearchUIIntent.LoadNextPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
    ) {
        SearchTopBar(onBackClick = { onIntent(SearchUIIntent.BackClicked) })

        SearchInputBar(
            query = state.query,
            onQueryChange = { onIntent(SearchUIIntent.QueryChanged(it)) },
            onClearClick = { onIntent(SearchUIIntent.ClearQueryClicked) },
            modifier = Modifier.padding(top = 8.dp),
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.End
        ) {
            SearchFilterChips(
                chips = state.filters,
                onChipClick = { onIntent(SearchUIIntent.FilterChipClicked(it)) },
            )
        }

        SearchResultsHeader(
            resultsCount = state.resultsCount,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    com.medsy.designsystem.components.MedsyShimmer(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            repeat(5) {
                                com.medsy.designsystem.components.MedsyShimmerPlaceholder(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(vertical = 8.dp),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }

                state.errorMessage != null -> {
                    SearchErrorState(
                        messageRes = state.errorMessage,
                        onRetry = { onIntent(SearchUIIntent.RetryClicked) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                state.isEmpty -> {
                    SearchEmptyState(modifier = Modifier.fillMaxSize())
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products, key = { it.id }) { product ->
                            ProductResultCard(
                                name = product.name,
                                subtitle = product.subtitle,
                                imageUrl = product.imageUrl,
                                priceEgp = product.priceEgp,
                                isFavorite = product.id in state.favoriteProductIds,
                                onClick = { onIntent(SearchUIIntent.ProductClicked(product.id)) },
                                onFavoriteClick = { onIntent(SearchUIIntent.FavoriteClicked(product.id)) },
                                onAddToCartClick = {
                                    onIntent(
                                        SearchUIIntent.AddToCartClicked(
                                            product.id
                                        )
                                    )
                                },
                            )
                        }

                        if (state.isLoadMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isSortBottomSheetOpen) {
        SearchSelectionBottomSheet(
            titleRes = R.string.search_sort_title,
            options = SortOption.values().toList(),
            selectedOption = state.selectedSort,
            optionLabelRes = { it.labelResId },
            onOptionClick = { onIntent(SearchUIIntent.SortOptionSelected(it)) },
            onDismissRequest = { onIntent(SearchUIIntent.DismissBottomSheet) }
        )
    }

    if (state.isCategoryBottomSheetOpen) {
        SearchCategoryBottomSheet(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = { onIntent(SearchUIIntent.CategoryOptionSelected(it)) },
            onDismissRequest = { onIntent(SearchUIIntent.DismissBottomSheet) }
        )
    }

}

@Composable
fun SearchErrorState(
    messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.search_error_retry),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
