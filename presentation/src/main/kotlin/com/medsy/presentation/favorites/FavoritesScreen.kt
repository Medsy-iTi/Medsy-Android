package com.medsy.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import com.medsy.presentation.R
import com.medsy.presentation.search.components.ProductResultCard

@Composable
fun FavoritesRoot(
    onBack: () -> Unit,
    onProductSelected: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FavoritesUIEffect.NavigateBack -> onBack()
                is FavoritesUIEffect.NavigateToProductDetails -> onProductSelected(effect.productId)
                is FavoritesUIEffect.ShowMessage -> {
                    val message = if (effect.args.isEmpty()) {
                        ContextCompat.getString(context, effect.messageRes)
                    } else {
                        context.getString(effect.messageRes, *effect.args.toTypedArray())
                    }
                    snackbarHostState.showSuccess(message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { MedsySnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        FavoritesScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onIntent: (FavoritesUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.profile_my_favourites),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                IconButton(onClick = { onIntent(FavoritesUIIntent.BackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.content_desc_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                state.isEmpty -> {
                    Text(
                        text = stringResource(R.string.favorites_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products, key = { it.id }) { product ->
                            ProductResultCard(
                                name = product.name,
                                subtitle = product.subtitle,
                                imageUrl = product.imageUrl,
                                priceEgp = product.priceEgp,
                                isFavorite = true,
                                onClick = { onIntent(FavoritesUIIntent.ProductClicked(product.id)) },
                                onFavoriteClick = { onIntent(FavoritesUIIntent.FavoriteClicked(product.id)) },
                                onAddToCartClick = {
                                    onIntent(FavoritesUIIntent.AddToCartClicked(product.id))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
