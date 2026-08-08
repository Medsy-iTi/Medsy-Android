package com.medsy.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showMessage
import com.medsy.presentation.R
import kotlinx.coroutines.flow.collectLatest
import com.medsy.presentation.products.components.ProductGridCard
import com.medsy.presentation.products.components.ProductsTopBar
import com.medsy.presentation.products.components.ProductsShimmer

@Composable
fun ProductsRoot(
    categoryId: Int,
    categoryName: String,
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(categoryId, categoryName) {
        viewModel.onIntent(ProductsUIIntent.LoadProducts(categoryId, categoryName))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ProductsUIEffect.NavigateBack -> onBackClick()
                is ProductsUIEffect.NavigateToProductDetails -> onProductClick(effect.productId)
                is ProductsUIEffect.ShowMessage -> {
                    snackbarHostState.showMessage(
                        context = context,
                        messageRes = effect.messageRes,
                        isSuccess = effect.isSuccess,
                        args = effect.args,
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { MedsySnackbarHost(snackbarHostState) },
    ) { padding ->
        ProductsScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun ProductsScreen(
    state: ProductsUIState,
    onIntent: (ProductsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        ProductsShimmer()
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            ProductsTopBar(
                title = state.categoryName,
                onBackClick = { onIntent(ProductsUIIntent.OnBackClick) }
            )

            if (state.products.size > 10) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { onIntent(ProductsUIIntent.OnSearchQueryChange(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.home_search_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.errorMessageRes != null) {
                    Text(
                        text = stringResource(state.errorMessageRes),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.filteredProducts) { product ->
                            ProductGridCard(
                                product = product,
                                onClick = { onIntent(ProductsUIIntent.OnProductClick(product.id)) },
                                onAddToCart = {
                                    onIntent(ProductsUIIntent.OnAddToCartClick(product.id))
                                },
                                isFavorite = product.isFavorite,
                                onFavoriteClick = {
                                    onIntent(ProductsUIIntent.OnFavoriteClick(product.id))
                                },
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}
