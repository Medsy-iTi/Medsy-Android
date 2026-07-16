package com.medsy.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySearchBar
import com.medsy.presentation.R
import com.medsy.presentation.home.components.*

@Composable
fun HomeRoot(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAddressClick: () -> Unit,
    onUploadPrescriptionClick: () -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onCategoryClick: (String) -> Unit,

    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeUIEffect.NavigateToSearch -> onSearchClick()
                is HomeUIEffect.NavigateToCategory -> onCategoryClick(effect.categoryId)
                is HomeUIEffect.NavigateToNotifications -> onNotificationClick()
                is HomeUIEffect.NavigateToAddressSelection -> onAddressClick()
                is HomeUIEffect.NavigateToUploadPrescription -> onUploadPrescriptionClick()
                is HomeUIEffect.NavigateToCategories -> onViewAllCategoriesClick()
            }
        }
    }

    HomeScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun HomeScreen(
    state: HomeUIState,
    onIntent: (HomeUIIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(vertical = 24.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            HomeTopBar(
                deliveryAddress = state.deliveryAddress,
                notificationCount = state.notificationCount,
                onAddressClick = { onIntent(HomeUIIntent.OnAddressClick) },
                onNotificationClick = { onIntent(HomeUIIntent.OnNotificationClick) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            MedsySearchBar(
                hint = stringResource(R.string.home_search_hint),
                onSearchClick = { onIntent(HomeUIIntent.OnSearchFieldClick) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        PromoBannerCarousel(
            banners = state.banners,
            currentIndex = state.currentBannerIndex,
            onPromoClick = { onIntent(HomeUIIntent.OnPromoClick) }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            OrderCardsSection(
                onSearchMedicineClick = { onIntent(HomeUIIntent.OnSearchMedicineClick) },
                onUploadPrescriptionClick = { onIntent(HomeUIIntent.OnUploadPrescriptionClick) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            CategoriesSection(
                categories = state.categories,
                onViewAllClick = { onIntent(HomeUIIntent.OnViewAllCategoriesClick) },
                onCategoryClick = { onIntent(HomeUIIntent.OnCategoryClick(it)) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            FastDeliveryBanner()
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

