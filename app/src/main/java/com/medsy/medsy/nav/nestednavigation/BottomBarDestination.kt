package com.medsy.medsy.nav.nestednavigation

import com.medsy.medsy.nav.rootnavigation.Route

enum class BottomBarDestination(
    val title: String,
    val icon: Int,
    val selectedIcon: Int,
    val route: Route
) {
    Home(
        title = "Home",
        icon = android.R.drawable.ic_menu_view,
        selectedIcon = android.R.drawable.ic_menu_view,
        route = Route.NestedNav.Home
    ),
    Cart(
        title = "Cart",
        icon = android.R.drawable.ic_menu_add,
        selectedIcon = android.R.drawable.ic_menu_add,
        route = Route.NestedNav.Cart
    ),
    Profile(
        title = "Profile",
        icon = android.R.drawable.ic_menu_myplaces,
        selectedIcon = android.R.drawable.ic_menu_myplaces,
        route = Route.NestedNav.Profile
    )
}
