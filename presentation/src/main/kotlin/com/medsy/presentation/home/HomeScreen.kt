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
import androidx.compose.material3.ExperimentalMaterial3Api
import com.medsy.designsystem.components.MedsySearchBar
import com.medsy.presentation.R
import com.medsy.presentation.home.components.*

@Composable
fun HomeRoot(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAddressClick: () -> Unit,
    onUploadPrescriptionClick: () -> Unit,
    onMedicineImageSearchClick: () -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onCategoryClick: (Int, String) -> Unit,
    onViewOffersClick: () -> Unit,

    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeUIEffect.NavigateToSearch -> onSearchClick()
                is HomeUIEffect.NavigateToCategory -> onCategoryClick(
                    effect.categoryId.toInt(),
                    effect.categoryName
                )

                is HomeUIEffect.NavigateToNotifications -> onNotificationClick()
                is HomeUIEffect.NavigateToAddressSelection -> onAddressClick()
                is HomeUIEffect.NavigateToUploadPrescription -> onUploadPrescriptionClick()
                is HomeUIEffect.NavigateToMedicineImageSearch -> onMedicineImageSearchClick()
                is HomeUIEffect.NavigateToCategories -> onViewAllCategoriesClick()
                is HomeUIEffect.NavigateToOffers -> onViewOffersClick()
            }
        }
    }

    HomeScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUIState,
    onIntent: (HomeUIIntent) -> Unit
) {
    if (state.isLoading) {
        HomeShimmer()
    } else {
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

            if (state.activeSearchStatus !is ActiveSearchStatus.Idle) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    ActiveSearchCard(
                        status = state.activeSearchStatus,
                        onCancelClick = { onIntent(HomeUIIntent.OnCancelSearchSimulation) },
                        onViewOffersClick = { onIntent(HomeUIIntent.OnViewOffersClick) },
                        onSearchWiderRangeClick = { onIntent(HomeUIIntent.OnSearchWiderRangeClick) }
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

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
}

