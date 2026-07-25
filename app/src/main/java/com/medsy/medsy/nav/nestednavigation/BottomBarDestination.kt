package com.medsy.medsy.nav.nestednavigation

import com.medsy.medsy.nav.rootnavigation.Route
import com.medsy.medsy.R as RApp
import com.medsy.presentation.R as RPresentation

enum class BottomBarDestination(
    val title: Int,
    val icon: Int,
    val selectedIcon: Int,
    val route: Route,
    val isProminent: Boolean = false,
) {
    Home(
        title = RApp.string.home,
        icon = RPresentation.drawable.home,
        selectedIcon = RPresentation.drawable.home,
        route = Route.NestedNav.Home
    ),
    Cart(
        title = RApp.string.cart,
        icon = RPresentation.drawable.cart,
        selectedIcon = RPresentation.drawable.cart,
        route = Route.NestedNav.Cart
    ),
    AiChat(
        title = RApp.string.ai_chat,
        icon = RPresentation.drawable.home,
        selectedIcon = RPresentation.drawable.home,
        route = Route.AiChat,
        isProminent = true,
    ),
    Orders(
        title = RApp.string.orders,
        icon = RPresentation.drawable.orderlist,
        selectedIcon = RPresentation.drawable.orderlist,
        route = Route.NestedNav.Orders,
    ),
    Profile(
        title = RApp.string.profile,
        icon = RPresentation.drawable.person,
        selectedIcon = RPresentation.drawable.person,
        route = Route.NestedNav.Profile
    )
}
