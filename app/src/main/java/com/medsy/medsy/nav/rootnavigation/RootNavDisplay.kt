package com.medsy.medsy.nav.rootnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.medsy.medsy.nav.NAVIGATION_DURATION_MILLIS
import com.medsy.medsy.nav.nestednavigation.NestedNavDisplay
import com.medsy.presentation.aichat.AiChatRoot
import com.medsy.presentation.auth.login.LoginRoot
import com.medsy.presentation.auth.otp.OtpRoot
import com.medsy.presentation.auth.register.RegisterRoot
import com.medsy.presentation.cart.CartRoot
import com.medsy.presentation.categories.CategoriesRoot
import com.medsy.presentation.onboarding.OnboardingRoot
import com.medsy.presentation.pharmacyprofile.PharmacyProfileRoot
import com.medsy.presentation.prescription.PrescriptionRoot
import com.medsy.presentation.productdetails.ProductDetailsRoot
import com.medsy.presentation.products.ProductsRoot
import com.medsy.presentation.profile.personaldetails.PersonalDetailsRoot
import com.medsy.presentation.search.SearchRoot
import com.medsy.presentation.settings.SettingsRoot
import com.medsy.presentation.splash.SplashRoot

@Composable
fun RootNavDisplay() {
    val context = LocalContext.current
    val rootBackStack: NavBackStack<NavKey> = rememberNavBackStack(Route.Splash)

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = rootBackStack,
        onBack = { rootBackStack.onBack(context) },
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
                    },
                    openHome = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.NestedNav)
                        }
                    },
                    openLogin = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.Login)
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
                    onNavigateToOtp = { email -> rootBackStack.navigateSingleTop(Route.Otp(email)) }
                )
            }
            entry<Route.Otp> { route ->
                OtpRoot(
                    email = route.email,
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateHome = {
                        rootBackStack.apply {
                            clear()
                            navigateSingleTop(Route.NestedNav)
                        }
                    }
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    navigateBack = {
                        rootBackStack.onBack(context)
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
                    openProducts = { categoryId, categoryName ->
                        rootBackStack.navigateSingleTop(Route.Products(categoryId, categoryName))
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
            entry<Route.ProductDetails> { route ->
                ProductDetailsRoot(
                    productId = route.id,
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToCart = {
                    },
                    onNavigateToPharmacistChat = {
                        rootBackStack.navigateSingleTop(Route.AiChat(/* required params here */))
                    },
                )
            }
            entry<Route.PharmacyProfile> { route ->
                PharmacyProfileRoot(
                    pharmacyId = route.pharmacyId,
                    onNavigateBack = { rootBackStack.onBack(context) },
                    onDialPhone = context::openDialer,
                    onOpenDirections = context::openDirections,
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
                    onNext = { productId ->
                        rootBackStack.navigateSingleTop(
                            Route.ProductDetails(id = productId)
                        )
                    },
                    onBack = { rootBackStack.removeLastOrNull() },
                )
            }
            entry<Route.Categories> {
                CategoriesRoot(
                    onBackClick = { rootBackStack.removeLastOrNull() },
                    onCategoryClick = { categoryId, categoryName ->
                        rootBackStack.navigateSingleTop(Route.Products(categoryId, categoryName))
                    }
                )
            }
            entry<Route.Products> { route ->
                ProductsRoot(
                    categoryId = route.categoryId,
                    categoryName = route.categoryName,
                    onBackClick = { rootBackStack.removeLastOrNull() },
                    onProductClick = { productId ->
                        rootBackStack.navigateSingleTop(Route.ProductDetails(productId.toString()))
                    }
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
