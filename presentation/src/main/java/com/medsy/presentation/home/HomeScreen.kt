package com.medsy.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.home.components.*

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeUIEffect.NavigateToSearch -> onNavigateToSearch()
                else -> {}
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        HomeScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun HomeScreen(
    state: HomeUIState,
    onAction: (HomeUIIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        HomeTopBar(
            deliveryAddress = state.deliveryAddress,
            notificationCount = state.notificationCount,
            onAddressClick = { onAction(HomeUIIntent.OnAddressClick) },
            onNotificationClick = { onAction(HomeUIIntent.OnNotificationClick) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeSearchBar(
            onSearchClick = { onAction(HomeUIIntent.OnSearchFieldClick) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        PromoBannerCarousel(
            banners = state.banners,
            currentIndex = state.currentBannerIndex,
            onPromoClick = { onAction(HomeUIIntent.OnPromoClick) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        OrderCardsSection(
            onSearchMedicineClick = { onAction(HomeUIIntent.OnSearchMedicineClick) },
            onUploadPrescriptionClick = { onAction(HomeUIIntent.OnUploadPrescriptionClick) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        CategoriesSection(
            categories = state.categories,
            onViewAllClick = { onAction(HomeUIIntent.OnViewAllCategoriesClick) },
            onCategoryClick = { onAction(HomeUIIntent.OnCategoryClick(it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        FastDeliveryBanner()
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
