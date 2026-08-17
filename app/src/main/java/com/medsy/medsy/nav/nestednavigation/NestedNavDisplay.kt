package com.medsy.medsy.nav.nestednavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.medsy.medsy.nav.rootnavigation.NAVIGATION_DURATION_MILLIS
import com.medsy.medsy.nav.rootnavigation.Route
import com.medsy.medsy.nav.rootnavigation.pop
import com.medsy.medsy.nav.rootnavigation.push
import com.medsy.medsy.nav.rootnavigation.setRoot
import com.medsy.presentation.cart.CartRoot
import com.medsy.presentation.cart.CartBadgeViewModel
import com.medsy.presentation.home.HomeRoot
import com.medsy.presentation.orders.orderslist.OrdersRoot
import com.medsy.presentation.profile.ProfileRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Composable
fun NestedNavDisplay(
    navigateBack: () -> Unit,
    openPersonalDetails: (startInEditMode: Boolean) -> Unit,
    openLogin: () -> Unit,
    openAiChat: () -> Unit,
    openSearch: () -> Unit,
    openCategories: () -> Unit,
    openProducts: (Int, String) -> Unit,
    openOffers: (Long) -> Unit,
    openOrderReview: (Long, Long) -> Unit,
    openPrescription: (Boolean, Boolean) -> Unit,
    openCartRequest: () -> Unit,
    requestedDestination: Route?,
    onRequestedDestinationHandled: () -> Unit,
    openOrderDetails: (String) -> Unit,
    openFavorites: () -> Unit,
    openReminders: () -> Unit,
    cartBadgeViewModel: CartBadgeViewModel = hiltViewModel()
) {
    val cartBadgeCount by cartBadgeViewModel.cartItemCount.collectAsStateWithLifecycle()

    val nestedBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.NestedNav.Home::class, Route.NestedNav.Home.serializer())
                    subclass(Route.NestedNav.Cart::class, Route.NestedNav.Cart.serializer())
                    subclass(Route.NestedNav.Orders::class, Route.NestedNav.Orders.serializer())
                    subclass(Route.NestedNav.Profile::class, Route.NestedNav.Profile.serializer())
                }
            }
        },
        Route.NestedNav.Home
    )

    LaunchedEffect(requestedDestination) {
        val destination = requestedDestination ?: return@LaunchedEffect
        nestedBackStack.apply {
            setRoot(Route.NestedNav.Home)
            if (destination != Route.NestedNav.Home) {
                push(destination)
            }
        }
        onRequestedDestinationHandled()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = 0.dp,
            ) {
                BottomBarDestination.entries.forEachIndexed { index, destination ->
                    val isSelected = nestedBackStack.lastOrNull() == destination.route

                    if (index != 2) {
                        BottomNavigationButton(
                            onClick = {
                                nestedBackStack.apply {
                                    setRoot(Route.NestedNav.Home)
                                    if (destination.route != Route.NestedNav.Home) {
                                        push(destination.route)
                                    }
                                }
                            },
                            icon = if (isSelected) destination.selectedIcon else destination.icon,
                            modifier = Modifier.weight(1f),
                            selected = isSelected,
                            label = destination.title,
                            badgeCount = if (destination.route == Route.NestedNav.Cart) cartBadgeCount else 0
                        )
                    } else {
                        AiChatNavigationButton(
                            onClick = openAiChat,
                            modifier = Modifier.weight(1f),
                            selected = isSelected,
                            label = destination.title,
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            backStack = nestedBackStack,
            onBack = {
                if (!nestedBackStack.pop()) {
                    navigateBack()
                }
            },
            transitionSpec = {
                fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                        fadeOut(tween(NAVIGATION_DURATION_MILLIS))
            },
            popTransitionSpec = {
                (slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                    initialOffset = { it / 3 },
                ) + fadeIn(tween(NAVIGATION_DURATION_MILLIS))) togetherWith
                        (slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                            targetOffset = { it / 3 },
                        ) + fadeOut(tween(NAVIGATION_DURATION_MILLIS)))
            },
            predictivePopTransitionSpec = { _ ->
                (slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                    initialOffset = { it / 3 },
                ) + fadeIn(tween(NAVIGATION_DURATION_MILLIS))) togetherWith
                        (slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                            targetOffset = { it / 3 },
                        ) + fadeOut(tween(NAVIGATION_DURATION_MILLIS)))
            },
            entryProvider = entryProvider {
                entry<Route.NestedNav.Home> {
                    HomeRoot(
                        onSearchClick = { openSearch() },
                        onNotificationClick = { /* Handle notification click */ },
                        onAddressClick = { /* Handle address click */ },
                        onUploadPrescriptionClick = { openPrescription(false, false) },
                        onMedicineImageSearchClick = { openPrescription(false, true) },
                        onViewAllCategoriesClick = { openCategories() },
                        onCategoryClick = { categoryId, categoryName ->
                            openProducts(categoryId, categoryName)
                        },
                        onViewOffersClick = { requestId -> openOffers(requestId) },
                        onResumeOrderReview = openOrderReview,
                    )
                }
                entry<Route.NestedNav.Cart> {
                    CartRoot(
                        onAddPrescription = { openPrescription(true, false) },
                        onMedicineSearch = { openPrescription(false, true) },
                        onOpenCartRequest = openCartRequest,
                    )
                }
                entry<Route.NestedNav.Orders> {
                    OrdersRoot(
                        onOpenOrderDetails = openOrderDetails,
                        onOpenOrderReview = openOrderReview,
                    )
                }
                entry<Route.NestedNav.Profile> {
                    ProfileRoot(
                        onNavigateToPersonalDetails = openPersonalDetails,
                        onNavigateToLogin = openLogin,
                        onNavigateToFavorites = openFavorites,
                        onNavigateToReminders = openReminders,
                    )
                }
            }
        )
    }
}
