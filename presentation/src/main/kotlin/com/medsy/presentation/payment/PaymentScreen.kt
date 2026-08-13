package com.medsy.presentation.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun PaymentScreenRoot(
    orderId: Long,
    publishableKey: String,
    onPaymentCompleted: () -> Unit,
    onPaymentFailed: (Int) -> Unit,
    onPaymentCanceled: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.onIntent(
            PaymentUIIntent.StartPayment(orderId)
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {

                PaymentUIEffect.PaymentCompleted -> {
                    onPaymentCompleted()
                }

                is PaymentUIEffect.PaymentFailed -> {
                    onPaymentFailed(effect.messageRes)
                }

                PaymentUIEffect.PaymentCanceled -> {
                    onPaymentCanceled()
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.errorMessageRes != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(state.errorMessageRes!!),
                    )
                    Spacer(Modifier.height(16.dp))
                    MedsyButton(
                        onClick = { viewModel.onIntent(PaymentUIIntent.Retry) }
                    ) {
                        Text(stringResource(R.string.offers_error_retry))
                    }
                }
            }

            state.clientSecret != null -> {
                PaymentSheetWrapper(
                    clientSecret = state.clientSecret!!,
                    publishableKey = publishableKey,
                    onPaymentCompleted = viewModel::onPaymentCompleted,
                    onPaymentFailed = viewModel::onPaymentFailed,
                    onPaymentCanceled = viewModel::onPaymentCanceled,
                )
            }
        }
    }
}

@Composable
private fun PaymentSheetWrapper(
    clientSecret: String,
    publishableKey: String,
    onPaymentCompleted: () -> Unit,
    onPaymentFailed: (Throwable) -> Unit,
    onPaymentCanceled: () -> Unit,
) {
    val context = LocalContext.current

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                onPaymentCompleted()
            }

            is PaymentSheetResult.Failed -> {
                onPaymentFailed(result.error)
            }

            is PaymentSheetResult.Canceled -> {
                onPaymentCanceled()
            }
        }
    }

    LaunchedEffect(clientSecret, publishableKey) {
        PaymentConfiguration.init(
            context,
            publishableKey,
        )

        val configuration =
            PaymentSheet.Configuration.Builder(
                merchantDisplayName = "Medsy",
            ).build()

        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            configuration,
        )
    }
}
