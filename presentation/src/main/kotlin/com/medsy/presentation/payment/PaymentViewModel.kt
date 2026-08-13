package com.medsy.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.payment.usecase.CreatePaymentIntentUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createPaymentIntentUseCase: CreatePaymentIntentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PaymentUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var currentOrderId: Long? = null

    fun onIntent(intent: PaymentUIIntent) {
        when (intent) {

            is PaymentUIIntent.StartPayment -> {
                createPaymentIntent(intent.orderId)
            }

            PaymentUIIntent.Retry -> {
                currentOrderId?.let(::createPaymentIntent)
            }
        }
    }

    private fun createPaymentIntent(orderId: Long) {
        currentOrderId = orderId

        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessageRes = null,
                    clientSecret = null,
                )
            }

            createPaymentIntentUseCase(orderId)
                .onSuccess { paymentIntent ->

                    _state.update {
                        it.copy(
                            isLoading = false,
                            clientSecret = paymentIntent.clientSecret,
                            errorMessageRes = null,
                        )
                    }
                }
                .onError { error ->

                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }

    fun onPaymentCompleted() {
        viewModelScope.launch {
            _effect.send(PaymentUIEffect.PaymentCompleted)
        }
    }

    fun onPaymentFailed(error: Throwable) {
        viewModelScope.launch {
            _effect.send(
                PaymentUIEffect.PaymentFailed(R.string.error_payment_failed)
            )
        }
    }

    fun onPaymentCanceled() {
        viewModelScope.launch {
            _effect.send(PaymentUIEffect.PaymentCanceled)
        }
    }
}