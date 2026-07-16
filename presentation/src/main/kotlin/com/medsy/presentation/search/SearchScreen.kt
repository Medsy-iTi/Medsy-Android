package com.medsy.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.search.components.ProductResultCard
import com.medsy.presentation.search.components.SearchEmptyState
import com.medsy.presentation.search.components.SearchFilterChips
import com.medsy.presentation.search.components.SearchInputBar
import com.medsy.presentation.search.components.SearchResultsHeader
import com.medsy.presentation.search.components.SearchTopBar
import kotlinx.coroutines.launch
@Composable
fun SearchRoot(
    onBack: () -> Unit,
    onNext: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SearchUIEffect.NavigateBack -> onBack()
                is SearchUIEffect.NavigateToProductDetails -> onNext(effect.productId)
                is SearchUIEffect.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

        SearchFilterChips(
            chips = state.filters,
            onChipClick = { onIntent(SearchUIIntent.FilterChipClicked(it)) },
            modifier = Modifier.padding(top = 16.dp),
        )

        SearchResultsHeader(
            resultsCount = state.resultsCount,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        if (state.isEmpty) {
            SearchEmptyState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(state.products, key = { it.id }) { product ->
                    ProductResultCard(
                        name = product.name,
                        subtitle = product.subtitle,
                        priceEgp = product.priceEgp,
                        isFavorite = product.id in state.favoriteProductIds,
                        onClick = { onIntent(SearchUIIntent.ProductClicked(product.id)) },
                        onFavoriteClick = { onIntent(SearchUIIntent.FavoriteClicked(product.id)) },
                        onAddToCartClick = { onIntent(SearchUIIntent.AddToCartClicked(product.id)) },
                    )
                }
            }
        }
    }
}
