package com.medsy.medsy.nav.rootnavigation

import android.content.Context
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> NavBackStack<T>.navigateSingleTop(
    route: T
) {
    if (lastOrNull() != route) {
        add(route)
    }
}

inline fun <reified T : NavKey> NavBackStack<*>.popIfCurrentIs() {
    if (lastOrNull() is T) {
        removeLastOrNull()
    }
}

fun NavBackStack<NavKey>.onBack(context: Context) {
    if (size > 1) {
        removeLastOrNull()
    } else {
        context.findActivity()?.finish()
    }
}
