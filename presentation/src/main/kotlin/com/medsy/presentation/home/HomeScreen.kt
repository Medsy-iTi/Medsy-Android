package com.medsy.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
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
    onResumeOrderReview: (Long, Long) -> Unit,

    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onIntent(HomeUIIntent.OnResume)
    }

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
                is HomeUIEffect.NavigateToOrderReview -> onResumeOrderReview(
                    effect.requestId,
                    effect.masterOrderId
                )
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

                if (state.resumableOrder != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                stringResource(R.string.home_continue_order_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                stringResource(R.string.home_continue_order_message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(12.dp))
                            MedsyButton(onClick = { onIntent(HomeUIIntent.OnContinueOrderClick) }) {
                                Text(stringResource(R.string.home_continue_order_action))
                            }
                        }
                    }
                } else if (state.activeSearchStatuses.firstOrNull() != null) {
                    val status = state.activeSearchStatuses.first()
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        ActiveSearchCard(
                            status = status,
                            onViewOffersClick = { onIntent(HomeUIIntent.OnViewOffersClick(status.requestId)) },
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

