package com.medsy.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySearchBar
import com.medsy.presentation.R
import com.medsy.presentation.home.components.ActiveSearchCard
import com.medsy.presentation.home.components.CategoriesSection
import com.medsy.presentation.home.components.FastDeliveryBanner
import com.medsy.presentation.home.components.HomeShimmer
import com.medsy.presentation.home.components.HomeTopBar
import com.medsy.presentation.home.components.OrderCardsSection
import com.medsy.presentation.home.components.PromoBannerCarousel

@Composable
fun HomeRoot(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAddressClick: () -> Unit,
    onUploadPrescriptionClick: () -> Unit,
    onMedicineImageSearchClick: () -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onCategoryClick: (Int, String) -> Unit,
    onViewOffersClick: (Long) -> Unit,

    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]

    LaunchedEffect(locale) {
        viewModel.onIntent(HomeUIIntent.LanguageChanged(locale.language))
    }

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
                is HomeUIEffect.NavigateToOffers -> onViewOffersClick(effect.requestId)
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
    if (state.isLoading && state.categories.isEmpty()) {
        HomeShimmer()
    } else {
        val scrollState = rememberScrollState()

        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onIntent(HomeUIIntent.RefreshData) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HomeTopBar(
                        deliveryAddress = state.deliveryAddress,
                        notificationCount = state.notificationCount,
                        onAddressClick = { onIntent(HomeUIIntent.OnAddressClick) },
                        onNotificationClick = { onIntent(HomeUIIntent.OnNotificationClick) }
                    )
                }


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

                if (state.activeSearchStatuses.firstOrNull() != null) {
                    val status = state.activeSearchStatuses.first()
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        ActiveSearchCard(
                            status = status,
                            onCancelClick = { onIntent(HomeUIIntent.OnCancelSearchSimulation(status.requestId)) },
                            onViewOffersClick = { onIntent(HomeUIIntent.OnViewOffersClick(status.requestId)) },
                            onSearchWiderRangeClick = { onIntent(HomeUIIntent.OnSearchWiderRangeClick) }
                        )
                    }
                } else {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OrderCardsSection(
                            onSearchMedicineClick = { onIntent(HomeUIIntent.OnSearchMedicineClick) },
                            onUploadPrescriptionClick = { onIntent(HomeUIIntent.OnUploadPrescriptionClick) }
                        )
                    }
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
}

