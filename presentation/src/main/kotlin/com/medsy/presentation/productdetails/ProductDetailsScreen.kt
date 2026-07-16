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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToPharmacistChat: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProductDetailsUIEffect.NavigateBack -> onNavigateBack()
                ProductDetailsUIEffect.NavigateToCart -> onNavigateToCart()
                ProductDetailsUIEffect.NavigateToPharmacistChat -> onNavigateToPharmacistChat()
                ProductDetailsUIEffect.OpenShareSheet -> { /* trigger platform share sheet */ }
                is ProductDetailsUIEffect.ShowMessage -> { /* handled below when hosted with Scaffold */ }
            }
        }
    }

    ProductDetailsScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToCart = onNavigateToCart,
        onNavigateToPharmacistChat = onNavigateToPharmacistChat,
    )
}

@Composable
fun ProductDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToPharmacistChat: () -> Unit,
    viewModel: ProductDetailsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProductDetailsUIEffect.NavigateBack -> onNavigateBack()
                ProductDetailsUIEffect.NavigateToCart -> onNavigateToCart()
                ProductDetailsUIEffect.NavigateToPharmacistChat -> onNavigateToPharmacistChat()
                ProductDetailsUIEffect.OpenShareSheet -> { /* trigger platform share sheet */ }
                is ProductDetailsUIEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        ProductDetailsContent(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun ProductDetailsContent(
    state: ProductDetailsUIState,
    onIntent: (ProductDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading || state.product == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
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
                placeholder = painterResource(id = R.drawable.panadaol_img)
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
                type = product.type,
                category = product.category,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        ProductBottomActions(
            isAddingToCart = state.isAddingToCart,
            onAddToCartClick = { onIntent(ProductDetailsUIIntent.AddToCartClicked) },
            onConsultPharmacistClick = { onIntent(ProductDetailsUIIntent.ConsultPharmacistClicked) },
        )
    }
}
