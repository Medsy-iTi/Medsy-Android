package com.medsy.presentation.cart.cartrequest

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
import com.medsy.presentation.cart.cartrequest.components.CartRequestOrderSummary
import com.medsy.presentation.cart.cartrequest.components.DeliveryAddressSection
import com.medsy.presentation.cart.cartrequest.components.FulfillmentSection
import com.medsy.presentation.cart.cartrequest.components.PaymentSection

@Composable
fun CartRequestRoot(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: CartRequestViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    BackHandler(enabled = state.isSubmitting) {}

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CartRequestUIEffect.NavigateHome -> onNavigateHome()
                is CartRequestUIEffect.ShowMessage -> {
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
                viewModel.onIntent(CartRequestUIIntent.LocationPickerDismissed)
            },
            onLocationConfirmed = { latitude, longitude ->
                viewModel.onIntent(
                    CartRequestUIIntent.LocationSelected(
                        latitude = latitude,
                        longitude = longitude,
                    )
                )
            },
        )
    } else {
        CartRequestScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            onNavigateBack = onNavigateBack,
            onIntent = viewModel::onIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartRequestScreen(
    state: CartRequestState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onIntent: (CartRequestUIIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.cart_request_title),
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
                            contentDescription = stringResource(R.string.cart_request_back),
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
                    onClick = { onIntent(CartRequestUIIntent.SubmitClicked) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    enabled = state.isSubmitEnabled,
                    isLoading = state.isSubmitting,
                    snackbarHostState = snackbarHostState,
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.cart_request_submit),
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
                            text = stringResource(R.string.cart_request_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                item {
                    CartRequestOrderSummary(
                        state = state,
                        onRetry = { onIntent(CartRequestUIIntent.RetryCart) },
                    )
                }
                item {
                    FulfillmentSection(
                        selected = state.deliveryMethod,
                        onSelected = {
                            onIntent(CartRequestUIIntent.DeliveryMethodSelected(it))
                        },
                    )
                }
                item {
                    DeliveryAddressSection(
                        state = state,
                        onAddressOptionSelected = {
                            onIntent(CartRequestUIIntent.AddressOptionSelected(it))
                        },
                        onCustomAddressChanged = {
                            onIntent(CartRequestUIIntent.CustomAddressChanged(it))
                        },
                        onChooseLocation = {
                            onIntent(CartRequestUIIntent.LocationPickerClicked)
                        },
                        onRetryProfile = {
                            onIntent(CartRequestUIIntent.RetryProfile)
                        },
                    )
                }
                item {
                    PaymentSection(
                        selected = state.paymentOption,
                        onSelected = {
                            onIntent(CartRequestUIIntent.PaymentOptionSelected(it))
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CartRequestScreenPreview() {
    CartRequestScreen(
        state = CartRequestState(
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
