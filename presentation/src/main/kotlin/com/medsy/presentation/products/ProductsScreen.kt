package com.medsy.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.OffWhiteBg
import com.medsy.presentation.R
import com.medsy.presentation.products.components.ProductListCard
import com.medsy.presentation.products.components.ProductsTopBar

@Composable
fun ProductsRoot(
    categoryId: Int,
    categoryName: String,
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(categoryId, categoryName) {
        viewModel.onIntent(ProductsUIIntent.LoadProducts(categoryId, categoryName))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProductsUIEffect.NavigateBack -> onBackClick()
                is ProductsUIEffect.NavigateToProductDetails -> onProductClick(effect.productId)
            }
        }
    }

    ProductsScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ProductsScreen(
    state: ProductsUIState,
    onIntent: (ProductsUIIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        ProductsTopBar(
            title = state.categoryName,
            onBackClick = { onIntent(ProductsUIIntent.OnBackClick) }
        )

        if (state.products.size > 10) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
                        focusedContainerColor = OffWhiteBg,
                        unfocusedContainerColor = OffWhiteBg,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(state.filteredProducts) { product ->
                        ProductListCard(
                            product = product,
                            onClick = { onIntent(ProductsUIIntent.OnProductClick(product.id)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
