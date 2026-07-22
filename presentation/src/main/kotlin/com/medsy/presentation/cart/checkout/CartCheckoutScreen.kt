package com.medsy.presentation.cart.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.location.MedsyLocationPickerScreen
import com.medsy.domain.cart.model.DeliveryMethod
import com.medsy.domain.cart.model.PaymentOption
import com.medsy.presentation.R
import com.medsy.presentation.cart.checkout.components.CheckoutOrderSummary
import com.medsy.presentation.cart.checkout.components.DeliveryAddressSection
import com.medsy.presentation.cart.checkout.components.FulfillmentSection
import com.medsy.presentation.cart.checkout.components.PaymentSection

@Composable
fun CartCheckoutRoot(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: CartCheckoutViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    BackHandler(enabled = state.isSubmitting) {}

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CartCheckoutUIEffect.NavigateHome -> onNavigateHome()
                is CartCheckoutUIEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        ContextCompat.getString(context, effect.messageRes)
                    )
                }
            }
        }
    }

    if (state.isMapPickerVisible) {
        MedsyLocationPickerScreen(
            initialLatitude = state.customLatitude,
            initialLongitude = state.customLongitude,
            onDismiss = {
                viewModel.onIntent(CartCheckoutUIIntent.LocationPickerDismissed)
            },
            onLocationConfirmed = { latitude, longitude ->
                viewModel.onIntent(
                    CartCheckoutUIIntent.LocationSelected(
                        latitude = latitude,
                        longitude = longitude,
                    )
                )
            },
        )
    } else {
        CartCheckoutScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            onNavigateBack = onNavigateBack,
            onIntent = viewModel::onIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    state: CartCheckoutState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onIntent: (CartCheckoutUIIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.checkout_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !state.isSubmitting,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.checkout_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                MedsyButton(
                    onClick = { onIntent(CartCheckoutUIIntent.SubmitClicked) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    enabled = state.isSubmitEnabled,
                    isLoading = state.isSubmitting,
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.checkout_submit),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 28.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.checkout_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                item {
                    CheckoutOrderSummary(
                        state = state,
                        onRetry = { onIntent(CartCheckoutUIIntent.RetryCart) },
                    )
                }
                item {
                    FulfillmentSection(
                        selected = state.deliveryMethod,
                        onSelected = {
                            onIntent(CartCheckoutUIIntent.DeliveryMethodSelected(it))
                        },
                    )
                }
                item {
                    DeliveryAddressSection(
                        state = state,
                        onAddressOptionSelected = {
                            onIntent(CartCheckoutUIIntent.AddressOptionSelected(it))
                        },
                        onCustomAddressChanged = {
                            onIntent(CartCheckoutUIIntent.CustomAddressChanged(it))
                        },
                        onChooseLocation = {
                            onIntent(CartCheckoutUIIntent.LocationPickerClicked)
                        },
                        onRetryProfile = {
                            onIntent(CartCheckoutUIIntent.RetryProfile)
                        },
                    )
                }
                item {
                    PaymentSection(
                        selected = state.paymentOption,
                        onSelected = {
                            onIntent(CartCheckoutUIIntent.PaymentOptionSelected(it))
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CartCheckoutScreenPreview() {
    CartCheckoutScreen(
        state = CartCheckoutState(
            isCartLoading = false,
            isProfileLoading = false,
            deliveryMethod = DeliveryMethod.DELIVERY,
            paymentOption = PaymentOption.CASH,
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onNavigateBack = {},
        onIntent = {},
    )
}
