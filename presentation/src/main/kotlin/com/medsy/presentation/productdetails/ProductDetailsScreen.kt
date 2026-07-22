package com.medsy.presentation.productdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.R
import com.medsy.presentation.productdetails.components.PharmacistNoticeCard
import com.medsy.presentation.productdetails.components.ProductBottomActions
import com.medsy.presentation.productdetails.components.ProductDetailsList
import com.medsy.presentation.productdetails.components.ProductImageCarousel
import com.medsy.presentation.productdetails.components.ProductInfoSection
import com.medsy.presentation.productdetails.components.ProductTitlePriceSection
import com.medsy.presentation.productdetails.components.ProductTopBar
import kotlinx.coroutines.flow.collectLatest


@Composable
fun ProductDetailsRoot(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPharmacistChat: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.init(productId)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProductDetailsUIEffect.NavigateBack -> onNavigateBack()
                ProductDetailsUIEffect.NavigateToPharmacistChat -> onNavigateToPharmacistChat()
                ProductDetailsUIEffect.OpenShareSheet -> { /* trigger platform share sheet */ }
                is ProductDetailsUIEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        ContextCompat.getString(context, effect.messageRes)
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        ProductDetailsScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun ProductDetailsScreen(
    state: ProductDetailsUIState,
    onIntent: (ProductDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.errorMessageRes != null || state.product == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(
                        state.errorMessageRes ?: R.string.product_details_error_load
                    )
                )
                TextButton(onClick = { onIntent(ProductDetailsUIIntent.RetryClicked) }) {
                    Text(text = stringResource(R.string.product_details_error_retry))
                }
            }
        }
        return
    }

    val product = state.product

    Column(modifier = modifier.fillMaxSize()) {
        ProductTopBar(
            onBackClick = { onIntent(ProductDetailsUIIntent.BackClicked) },
            onShareClick = { onIntent(ProductDetailsUIIntent.ShareClicked) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            ProductImageCarousel(
                imageUrls = product.imageUrls,
                selectedIndex = state.selectedImageIndex,
                isFavorite = state.isFavorite,
                onPageChanged = { onIntent(ProductDetailsUIIntent.ImagePageChanged(it)) },
                onFavoriteClick = { onIntent(ProductDetailsUIIntent.FavoriteClicked) },
                placeholder = painterResource(id = com.medsy.designsystem.R.drawable.ic_logo_transparent)
            )

            ProductTitlePriceSection(
                name = product.name,
                strength = product.strength,
                packInfo = product.packInfo,
                price = product.price,
                modifier = Modifier.padding(top = 12.dp),
            )

            PharmacistNoticeCard(modifier = Modifier.padding(top = 16.dp))

            ProductInfoSection(
                description = product.description,
                modifier = Modifier.padding(top = 20.dp),
            )

            ProductDetailsList(
                manufacturer = product.manufacturer,
                category = product.category,
                usage = product.route,
            )
        }

        ProductBottomActions(
            isAddingToCart = state.isAddingToCart,
            onAddToCartClick = { onIntent(ProductDetailsUIIntent.AddToCartClicked) },
            onConsultPharmacistClick = { onIntent(ProductDetailsUIIntent.ConsultPharmacistClicked) },
        )
    }
}
