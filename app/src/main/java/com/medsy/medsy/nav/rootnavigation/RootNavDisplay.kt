package com.medsy.medsy.nav.rootnavigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
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
import com.medsy.presentation.orders.details.OrderDetailsRoot
import com.medsy.presentation.pharmacyprofile.PharmacyProfileRoot
import com.medsy.presentation.prescription.PrescriptionRoot
import com.medsy.presentation.productdetails.ProductDetailsRoot
import com.medsy.presentation.products.ProductsRoot
import com.medsy.presentation.profile.personaldetails.PersonalDetailsRoot
import com.medsy.presentation.favorites.FavoritesRoot
import com.medsy.presentation.search.SearchRoot
import com.medsy.presentation.settings.SettingsRoot
import com.medsy.presentation.splash.SplashRoot

const val NAVIGATION_DURATION_MILLIS = 350

@Composable
fun RootNavDisplay() {

    val context = LocalContext.current
    val rootBackStack = rememberNavBackStack(Route.Splash)
    var requestedNestedDestination by remember { mutableStateOf<Route?>(null) }
    var prescriptionSelectionResult by remember { mutableStateOf<Pair<String, Int>?>(null) }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = rootBackStack,
        onBack = {
            if (!rootBackStack.pop()) {
                context.findActivity()?.finish()
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
        },
        popTransitionSpec = {
            fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
        },
        predictivePopTransitionSpec = { _ ->
            fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    openOnboarding = {
                        rootBackStack.setRoot(Route.Onboarding)
                    },
                    openHome = {
                        requestedNestedDestination = null
                        rootBackStack.setRoot(Route.NestedNav)
                    },
                    openLogin = {
                        rootBackStack.setRoot(Route.Login)
                    }
                )
            }

            entry<Route.Onboarding> {
                OnboardingRoot(
                    openLogin = {
                        rootBackStack.setRoot(Route.Login)
                    }
                )
            }
            entry<Route.Login> {
                LoginRoot(
                    openSignup = {
                        rootBackStack.push(Route.Register)
                    },
                    openHome = {
                        requestedNestedDestination = null
                        rootBackStack.setRoot(Route.NestedNav)
                    }
                )
            }



            entry<Route.Register> {
                RegisterRoot(
                    onNavigateBack = { rootBackStack.popIfCurrent(Route.Register) },
                    onNavigateToSignIn = {
                        if (rootBackStack.lastOrNull() == Route.Register) {
                            rootBackStack.replace(Route.Login)
                        }
                    },
                    onNavigateToOtp = { email -> rootBackStack.push(Route.Otp(email)) }
                )
            }
            entry<Route.Otp> { route ->
                OtpRoot(
                    email = route.email,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) },
                    onNavigateHome = {
                        requestedNestedDestination = null
                        rootBackStack.setRoot(Route.NestedNav)
                    }
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    navigateBack = {
                        if (!rootBackStack.pop()) {
                            context.findActivity()?.finish()
                        }
                    },
                    openSearch = {
                        rootBackStack.push(Route.SearchNav())
                    },
                    openPersonalDetails = { startInEditMode ->
                        rootBackStack.push(
                            Route.PersonalDetails(startInEditMode = startInEditMode)
                        )
                    },
                    openLogin = {
                        rootBackStack.setRoot(Route.Login)
                    },
                    openCategories = {
                        rootBackStack.push(Route.Categories)
                    },
                    openProducts = { categoryId, categoryName ->
                        rootBackStack.push(Route.Products(categoryId, categoryName))
                    },
                    openOrderDetails = { orderId ->
                        rootBackStack.push(Route.OrderDetails(orderId))
                    },
                    openOffers = { requestId ->
                        rootBackStack.push(Route.AvailableOffers(requestId))
                    },
                    openPrescription = { attachmentOnly, isMedicineSearch ->
                        rootBackStack.push(
                            Route.Prescription(attachmentOnly, isMedicineSearch)
                        )
                    },
                    openAiChat = {
                        rootBackStack.push(Route.AiChat())
                    },
                    openCartRequest = {
                        rootBackStack.push(Route.CartRequest)
                    },
                    requestedDestination = requestedNestedDestination,
                    onRequestedDestinationHandled = {
                        requestedNestedDestination = null
                    },
                    openFavorites = {
                        rootBackStack.navigateSingleTop(Route.Favorites)
                    }
                )
            }
            entry<Route.AiChat> { route ->
                AiChatRoot(
                    initialPrompt = route.initialPrompt,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) },
                    onOpenProduct = { productId ->
                        rootBackStack.push(
                            Route.ProductDetails(id = productId.toString())
                        )
                    },
                    onOpenCategory = { categoryId, categoryName ->
                        rootBackStack.push(
                            Route.Products(categoryId, categoryName)
                        )
                    },
                    onOpenCartTab = {
                        rootBackStack.navigateToNestedDestination(Route.NestedNav.Cart) {
                            requestedNestedDestination = it
                        }
                    },
                    onOpenCartRequest = {
                        rootBackStack.push(Route.CartRequest)
                    },
                    onDial = context::openDialer,
                )
            }
            entry<Route.OrderDetails> { route ->
                OrderDetailsRoot(
                    orderId = route.orderId,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) },
                    onNavigateToPharmacyProfile = { id ->
                        rootBackStack.push(Route.PharmacyProfile(id))
                    },
                    onReorder = {
                        rootBackStack.navigateToNestedDestination(Route.NestedNav.Cart) {
                            requestedNestedDestination = it
                        }
                    },
                    onNavigateToProductDetails = { productId ->
                        rootBackStack.push(Route.ProductDetails(id = productId))
                    },
                )
            }

            entry<Route.ProductDetails> { route ->
                ProductDetailsRoot(
                    productId = route.id,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) },
                    onNavigateToPharmacistChat = {
                        if (rootBackStack.lastOrNull() == route) {
                            rootBackStack.replace(Route.AiChat())
                        }
                    },
                )
            }
            entry<Route.PharmacyProfile> { route ->
                PharmacyProfileRoot(
                    pharmacyId = route.pharmacyId,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) },
                    onDialPhone = context::openDialer,
                    onOpenDirections = context::openDirections,
                )
            }
            entry<Route.Settings> {
                SettingsRoot(
                    onNext = { rootBackStack.popIfCurrent(Route.Settings) }
                )
            }
            entry<Route.CartRequest> {
                CartRequestRoot(
                    onNavigateBack = { rootBackStack.popIfCurrent(Route.CartRequest) },
                    onNavigateHome = {
                        rootBackStack.navigateToNestedDestination(Route.NestedNav.Home) {
                            requestedNestedDestination = it
                        }
                    },
                )
            }
            entry<Route.PersonalDetails> { route ->
                PersonalDetailsRoot(
                    startInEditMode = route.startInEditMode,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) }
                )
            }
            entry<Route.Favorites> {
                FavoritesRoot(
                    onBack = { rootBackStack.removeLastOrNull() },
                    onProductSelected = { productId ->
                        rootBackStack.navigateSingleTop(
                            Route.ProductDetails(id = productId)
                        )
                    }
                )
            }
            entry<Route.SearchNav> { route ->
                SearchRoot(
                    initialQuery = route.initialQuery,
                    onNext = { productId ->
                        rootBackStack.push(
                            Route.ProductDetails(id = productId)
                        )
                    },
                    onBack = { rootBackStack.popIfCurrent(route) },
                    onProductSelected = if (route.localItemId != null) {
                        { productId ->
                            prescriptionSelectionResult = route.localItemId to productId.toInt()
                            rootBackStack.popIfCurrent(route)
                        }
                    } else null
                )
            }
            entry<Route.Categories> {
                CategoriesRoot(
                    onBackClick = { rootBackStack.popIfCurrent(Route.Categories) },
                    onCategoryClick = { categoryId, categoryName ->
                        rootBackStack.push(Route.Products(categoryId, categoryName))
                    }
                )
            }
            entry<Route.Products> { route ->
                ProductsRoot(
                    categoryId = route.categoryId,
                    categoryName = route.categoryName,
                    onBackClick = { rootBackStack.popIfCurrent(route) },
                    onProductClick = { productId ->
                        rootBackStack.push(Route.ProductDetails(productId.toString()))
                    }
                )
            }
            entry<Route.Prescription> { route ->
                PrescriptionRoot(
                    attachmentOnly = route.attachmentOnly,
                    isMedicineSearch = route.isMedicineSearch,
                    resultLocalItemId = prescriptionSelectionResult?.first,
                    resultProductId = prescriptionSelectionResult?.second,
                    onNavigateBack = { rootBackStack.popIfCurrent(route) },
                    onNavigateHome = {
                        rootBackStack.navigateToNestedDestination(Route.NestedNav.Home) {
                            requestedNestedDestination = it
                        }
                    },
                    onNavigateCart = {
                        rootBackStack.navigateToNestedDestination(Route.NestedNav.Cart) {
                            requestedNestedDestination = it
                        }
                    },
                    onPrescriptionAttached = {
                        rootBackStack.popIfCurrent(route)
                    },
                    onNavigateToSearch = { query, localItemId ->
                        rootBackStack.push(
                            Route.SearchNav(initialQuery = query, localItemId = localItemId)
                        )
                    },
                    onNavigateToProductDetails = { productId ->
                        rootBackStack.push(
                            Route.ProductDetails(id = productId)
                        )
                    },
                    onResultHandled = {
                        prescriptionSelectionResult = null
                    }
                )
            }
            entry<Route.AvailableOffers> {
                val args = it
                AvailableOffersRoot(
                    requestId = args.requestId,
                    onNavigateBack = { rootBackStack.popIfCurrent(args) },
                    onNavigateToOfferDetails = { offerId ->
                        rootBackStack.push(Route.OfferDetails(args.requestId, offerId))
                    }
                )
            }
            entry<Route.OfferDetails> {
                val args = it
                OfferDetailsRoot(
                    requestId = args.requestId,
                    offerId = args.offerId,
                    onNavigateBack = { rootBackStack.popIfCurrent(args) },
                    onNavigateToOrderReview = { reqId, offId ->
                        rootBackStack.push(
                            Route.OrderReview(reqId, offId)
                        )
                    }
                )
            }
            entry<Route.OrderReview> {
                val args = it
                OrderReviewRoot(
                    requestId = args.requestId,
                    offerId = args.offerId,
                    onNavigateBack = { rootBackStack.popIfCurrent(args) },
                    onNavigateToOrderConfirmation = { orderId, pharmacyName ->
                        rootBackStack.push(
                            Route.OrderConfirmation(
                                orderId,
                                pharmacyName
                            )
                        )
                    }
                )
            }
            entry<Route.OrderConfirmation> { route ->
                OrderConfirmationRoot(
                    orderId = route.orderId,
                    pharmacyName = route.pharmacyName,
                    onNavigateToTrackOrder = {
                        rootBackStack.setRoot(Route.NestedNav)
                    },
                    onNavigateToHome = { rootBackStack.setRoot(Route.NestedNav) }
                )
            }
        }
    )
}
