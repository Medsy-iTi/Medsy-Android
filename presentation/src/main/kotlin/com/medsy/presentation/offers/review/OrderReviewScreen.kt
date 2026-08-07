package com.medsy.presentation.offers.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder
import com.medsy.designsystem.components.showError
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.presentation.R
import com.medsy.presentation.common.util.PriceFormatter
import com.medsy.presentation.offers.OffersState
import com.medsy.presentation.offers.OffersUIEffect
import com.medsy.presentation.offers.OffersUIIntent
import com.medsy.presentation.offers.OffersViewModel
import com.medsy.presentation.offers.components.MedicineItemRow
import com.medsy.presentation.offers.components.OfferTopAppBar
import com.medsy.presentation.offers.components.PriceSummarySection
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun OrderReviewRoot(
    requestId: Long,
    offerId: String,
    onNavigateBack: () -> Unit,
    onNavigateToOrderConfirmation: (String, String) -> Unit,
    viewModel: OffersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val paymentSheet = rememberPaymentSheet { paymentResult: PaymentSheetResult ->
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                viewModel.onIntent(OffersUIIntent.PaymentSuccess)
            }

            is PaymentSheetResult.Canceled -> {
                viewModel.onIntent(OffersUIIntent.PaymentCanceled)
            }

            is PaymentSheetResult.Failed -> {
                viewModel.onIntent(OffersUIIntent.PaymentFailed(paymentResult.error.localizedMessage))
            }
        }
    }

    LaunchedEffect(requestId) {
        viewModel.onIntent(OffersUIIntent.LoadOfferDetails(requestId, offerId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OffersUIEffect.NavigateBack -> onNavigateBack()
                is OffersUIEffect.NavigateToOrderConfirmation -> onNavigateToOrderConfirmation(
                    effect.orderId,
                    effect.pharmacyName
                )

                is OffersUIEffect.ShowError -> {
                    snackbarHostState.showError(
                        androidx.core.content.ContextCompat.getString(
                            context,
                            effect.messageRes
                        )
                    )
                }

                is OffersUIEffect.OpenPaymentSheet -> {
                    paymentSheet.presentWithPaymentIntent(
                        effect.clientSecret,
                        PaymentSheet.Configuration(
                            merchantDisplayName = "Medsy"
                        )
                    )
                }

                else -> Unit
            }
        }
    }

    OrderReviewScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun OrderReviewScreen(
    state: OffersState,
    onIntent: (OffersUIIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val offer = state.selectedOffer
    val locale = LocalConfiguration.current.locales[0]

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                OfferTopAppBar(
                    title = stringResource(R.string.offers_order_review_title),
                    onBackClick = { onIntent(OffersUIIntent.NavigateBack) }
                )
            },
            bottomBar = {
                if (offer != null) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        com.medsy.designsystem.components.MedsyButton(
                            onClick = { onIntent(OffersUIIntent.ConfirmOrder) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !state.isConfirmingOrder,
                            snackbarHostState = snackbarHostState
                        ) {
                            if (state.isConfirmingOrder) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.offers_confirm_order),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading || offer == null) {
                    MedsyShimmer(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MedsyShimmerPlaceholder(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    MedsyShimmerPlaceholder(
                                        modifier = Modifier.height(20.dp).fillMaxWidth(0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    MedsyShimmerPlaceholder(
                                        modifier = Modifier.height(14.dp).fillMaxWidth(0.3f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            MedsyShimmerPlaceholder(
                                modifier = Modifier.height(24.dp).fillMaxWidth(0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            repeat(3) {
                                MedsyShimmerPlaceholder(
                                    modifier = Modifier.fillMaxWidth().height(80.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    ) {


                        item {
                            Text(
                                text = stringResource(R.string.offers_requested_medicines),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(offer.medicines.filter { it.isAvailable }) { medicine ->
                            MedicineItemRow(medicine = medicine, isSingleLinePrice = true)
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = stringResource(R.string.offers_delivery_address),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = stringResource(R.string.home_address_mock),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = stringResource(R.string.offers_delivery_address_mock),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = stringResource(R.string.offers_delivery_details),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = stringResource(R.string.offers_home_delivery),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = PriceFormatter.formatPrice(state.deliveryFee, locale),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = " ${stringResource(R.string.currency_egp)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            PriceSummarySection(
                                medicinesPrice = offer.price,
                                deliveryFee = state.deliveryFee
                            )
                        }
                    }
                }
            }
        }
     MedsySnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}