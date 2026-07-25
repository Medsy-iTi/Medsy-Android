package com.medsy.medsy.nav.rootnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.medsy.presentation.auth.otp.OtpRoot
import com.medsy.presentation.auth.register.RegisterRoot
import com.medsy.presentation.cart.cartrequest.CartRequestRoot
import com.medsy.presentation.categories.CategoriesRoot
import com.medsy.presentation.offers.available.AvailableOffersRoot
import com.medsy.presentation.offers.confirmation.OrderConfirmationRoot
import com.medsy.presentation.offers.details.OfferDetailsRoot
import com.medsy.presentation.offers.review.OrderReviewRoot
import com.medsy.presentation.onboarding.OnboardingRoot
import com.medsy.presentation.prescription.PrescriptionRoot
import com.medsy.presentation.productdetails.ProductDetailsRoot
import com.medsy.presentation.products.ProductsRoot
import com.medsy.presentation.profile.personaldetails.PersonalDetailsRoot
import com.medsy.presentation.search.SearchRoot
import com.medsy.presentation.settings.SettingsRoot
import com.medsy.presentation.splash.SplashRoot

@Composable
fun RootNavDisplay() {
    val rootBackStack = rememberNavBackStack(Route.NestedNav)
    var requestedNestedDestination by remember { mutableStateOf<Route?>(null) }

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
                    },
                    openHome = {
                        rootBackStack.apply {
                            clear()
                            requestedNestedDestination = null
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
                        requestedNestedDestination = null
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
                            requestedNestedDestination = null
                            navigateSingleTop(Route.NestedNav)
                        }
                    }
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    navigateBack = {
                        rootBackStack.popIfCurrentIs<Route.NestedNav>()
                    },
                    openSearch = {
                        rootBackStack.navigateSingleTop(Route.SearchNav)
                    },
                    openPersonalDetails = { startInEditMode ->
                        rootBackStack.navigateSingleTop(
                            Route.PersonalDetails(startInEditMode = startInEditMode)
                        )
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
                    openOffers = {
                        rootBackStack.navigateSingleTop(Route.AvailableOffers)
                    },
                    openPrescription = { attachmentOnly ->
                        rootBackStack.navigateSingleTop(
                            Route.Prescription(attachmentOnly)
                        )
                    },
                    openAiChat = {
                        rootBackStack.navigateSingleTop(Route.AiChat)
                    },
                    openCartRequest = {
                        rootBackStack.navigateSingleTop(Route.CartRequest)
                    },
                    requestedDestination = requestedNestedDestination,
                    onRequestedDestinationHandled = {
                        requestedNestedDestination = null
                    }
                )
            }
            entry<Route.AiChat> {
                AiChatRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                )
            }
            entry<Route.ProductDetails> { route ->
                ProductDetailsRoot(
                    productId = route.id,
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToPharmacistChat = {
                        rootBackStack.removeLastOrNull()
                        rootBackStack.navigateSingleTop(Route.AiChat)
                    },
                )
            }
            entry<Route.Settings> {
                SettingsRoot(
                    onNext = { rootBackStack.removeLastOrNull() }
                )
            }
            entry<Route.CartRequest> {
                CartRequestRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateHome = {
                        requestedNestedDestination = Route.NestedNav.Home
                        rootBackStack.popIfCurrentIs<Route.CartRequest>()
                    },
                )
            }
            entry<Route.PersonalDetails> { route ->
                PersonalDetailsRoot(
                    startInEditMode = route.startInEditMode,
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
            entry<Route.Prescription> { route ->
                PrescriptionRoot(
                    attachmentOnly = route.attachmentOnly,
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateHome = {
                        rootBackStack.popIfCurrentIs<Route.Prescription>()
                    },
                    onNavigateCart = {
                        requestedNestedDestination = Route.NestedNav.Cart
                        rootBackStack.popIfCurrentIs<Route.Prescription>()
                    },
                    onPrescriptionAttached = {
                        rootBackStack.popIfCurrentIs<Route.Prescription>()
                    },
                )
            }
            entry<Route.AvailableOffers> {
                AvailableOffersRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToOfferDetails = { rootBackStack.navigateSingleTop(Route.OfferDetails) }
                )
            }
            entry<Route.OfferDetails> {
                OfferDetailsRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToOrderReview = { rootBackStack.navigateSingleTop(Route.OrderReview) }
                )
            }
            entry<Route.OrderReview> {
                OrderReviewRoot(
                    onNavigateBack = { rootBackStack.removeLastOrNull() },
                    onNavigateToOrderConfirmation = { rootBackStack.navigateSingleTop(Route.OrderConfirmation) }
                )
            }
            entry<Route.OrderConfirmation> {
                OrderConfirmationRoot(
                    onNavigateToTrackOrder = {
                        rootBackStack.apply {
                            clear(); navigateSingleTop(
                            Route.NestedNav
                        )
                        }
                    },
                    onNavigateToHome = { rootBackStack.apply { clear(); navigateSingleTop(Route.NestedNav) } }
                )
            }
        }
    )
}
