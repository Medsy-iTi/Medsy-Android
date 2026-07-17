package com.medsy.medsy.nav.rootnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.medsy.medsy.nav.NAVIGATION_DURATION_MILLIS
import com.medsy.medsy.nav.nestednavigation.NestedNavDisplay
import com.medsy.presentation.aichat.AiChatRoot
import com.medsy.presentation.auth.login.LoginRoot
import com.medsy.presentation.auth.register.RegisterRoot
import com.medsy.presentation.categories.CategoriesRoot
import com.medsy.presentation.cart.CartRoot
import com.medsy.presentation.onboarding.OnboardingRoot
import com.medsy.presentation.prescription.PrescriptionRoot
import com.medsy.presentation.productdetails.ProductDetailsRoot
import com.medsy.presentation.profile.personaldetails.PersonalDetailsRoot
import com.medsy.presentation.search.SearchRoot
import com.medsy.presentation.settings.SettingsRoot
import com.medsy.presentation.splash.SplashRoot

@Composable
fun RootNavDisplay() {
    val rootBackStack = rememberNavBackStack(Route.Splash)

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = rootBackStack,
        onBack = { rootBackStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS)
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS)
            )
        },
        popTransitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS)
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS)
            )
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    openOnboarding = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Onboarding)
                        }
                    }
                )
            }
            entry<Route.Onboarding> {
                OnboardingRoot(
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    }
                )
            }
            entry<Route.Login> {
                LoginRoot(
                    openSignup = {
                        rootBackStack.navigateSingleTop(Route.Register)
                    },
                    openHome = {
                        rootBackStack.navigateSingleTop(Route.NestedNav)
                    }
                )
            }



            entry<Route.Register> {
                RegisterRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToSignIn = { rootBackStack.navigateSingleTop(Route.Login) },
                    onNavigateToHome = { rootBackStack.navigateSingleTop(Route.NestedNav) }
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    navigateBack = {
                        rootBackStack.popIfCurrentIs<Route.NestedNav>()
                    },
                    openProductDetails = {
                        rootBackStack.navigateSingleTop(
                            Route.ProductDetails(id = "temporary-product-id")
                        )
                    },
                    openSearch = {
                        rootBackStack.navigateSingleTop(Route.SearchNav)
                    },
                    openPersonalDetails = {
                        rootBackStack.navigateSingleTop(Route.PersonalDetails)
                    },
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
                        }
                    },
                    openCategories = {
                        rootBackStack.navigateSingleTop(Route.Categories)
                    },
                    openPrescription = {
                        rootBackStack.navigateSingleTop(Route.Prescription)
                    }
                )
            }
            entry<Route.AiChat> {
                AiChatRoot(
                    onNext = { rootBackStack.removeLastOrNull() }
                )
            }
            entry<Route.ProductDetails> {
                ProductDetailsRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToCart = {
                        rootBackStack.navigateSingleTop(Route.NestedNav)
                    },
                    onNavigateToPharmacistChat = {

                    }
                )
            }
            entry<Route.Settings> {
                SettingsRoot(
                    onNext = { rootBackStack.removeLastOrNull() }
                )
            }
            entry<Route.PersonalDetails> {
                PersonalDetailsRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() }
                )
            }
            entry<Route.SearchNav> {
                SearchRoot(
                    onNext = {
                        rootBackStack.navigateSingleTop(
                            Route.ProductDetails(id = "temporary-product-id")
                        )
                    },
                    onBack = { rootBackStack.removeLastOrNull() },
                )
            }
            entry<Route.Categories> {
                CategoriesRoot(
                    onBackClick = { rootBackStack.removeLastOrNull() },
                    onCategoryClick = { categoryId -> /* Handle category click */ }
                )
            }
            entry<Route.Prescription> {
                PrescriptionRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateHome = {
                        rootBackStack.popIfCurrentIs<Route.Prescription>()
                    },
                    onNavigateCart = {
                        rootBackStack.navigateSingleTop(Route.NestedNav.Cart)
                    },
                )
            }
            entry<Route.NestedNav.Cart> {
                CartRoot(
                    onNext = {
                        rootBackStack.navigateSingleTop(
                            Route.ProductDetails(id = "temporary-product-id")
                        )
                    }
                )
            }
        }
    )
}
