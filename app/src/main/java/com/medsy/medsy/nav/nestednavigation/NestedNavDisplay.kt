package com.medsy.medsy.nav.nestednavigation

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
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.medsy.medsy.nav.NAVIGATION_DURATION_MILLIS
import com.medsy.medsy.nav.rootnavigation.Route
import com.medsy.medsy.nav.rootnavigation.navigateSingleTop
import com.medsy.presentation.cart.CartRoot
import com.medsy.presentation.home.HomeRoot
import com.medsy.presentation.orders.OrdersRoot
import com.medsy.presentation.profile.ProfileRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Composable
fun NestedNavDisplay(
    navigateBack: () -> Unit,
    openPersonalDetails: (startInEditMode: Boolean) -> Unit,
    openLogin: () -> Unit,
    openSearch: () -> Unit,
    openCategories: () -> Unit,
    openProducts: (Int, String) -> Unit,
    openOffers: () -> Unit,
    openPrescription: (Boolean, Boolean) -> Unit,
    openCartRequest: () -> Unit,
    requestedDestination: Route?,
    onRequestedDestinationHandled: () -> Unit,
    openOrderDetails: (String) -> Unit,
    ) {

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
            clear()
            if (destination != Route.NestedNav.Home) {
                navigateSingleTop(Route.NestedNav.Home)
            }
            navigateSingleTop(destination)
        }
        onRequestedDestinationHandled()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                BottomBarDestination.entries.forEach { destination ->
                    val isSelected = nestedBackStack.lastOrNull() == destination.route
                    BottomNavigationButton(
                        onClick = {
                            nestedBackStack.apply {
                                clear()
                                if (destination.route != Route.NestedNav.Home) {
                                    navigateSingleTop(Route.NestedNav.Home)
                                }
                                navigateSingleTop(destination.route)
                            }
                        },
                        icon = if (isSelected) destination.selectedIcon else destination.icon,
                        modifier = Modifier.weight(1f),
                        selected = isSelected,
                        label = destination.title
                    )
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
                if (nestedBackStack.lastOrNull() != Route.NestedNav.Home) {
                    nestedBackStack.removeLastOrNull()
                } else {
                    navigateBack()
                }
            },
            transitionSpec = {
                fadeIn(
                    tween(NAVIGATION_DURATION_MILLIS)
                ) togetherWith fadeOut(
                    tween(NAVIGATION_DURATION_MILLIS)
                )
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
                        onViewOffersClick = { openOffers() }
                    )
                }
                entry<Route.NestedNav.Cart> {
                    CartRoot(
                        onAddPrescription = { openPrescription(true, false) },
                        onOpenCartRequest = openCartRequest,
                    )
                }
                entry<Route.NestedNav.Orders> {
                    OrdersRoot(
                        onOrderClick = openOrderDetails,
                    )
                }

                entry<Route.NestedNav.Profile> {
                    ProfileRoot(
                        onNavigateToPersonalDetails = openPersonalDetails,
                        onNavigateToLogin = openLogin,
                    )
                }
            }
        )
    }
}
