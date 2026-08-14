package com.medsy.presentation.orders.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.offers.usecase.ConfirmFulfillmentUseCase
import com.medsy.domain.offers.usecase.GetRequestResultUseCase
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.usecase.DetermineOrderNextActionUseCase
import com.medsy.domain.orders.usecase.GetOrderByIdUseCase
import com.medsy.domain.requests.usecase.GetMedicineRequestByIdUseCase
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
class OrderReviewViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getMedicineRequestByIdUseCase: GetMedicineRequestByIdUseCase,
    private val getRequestResultUseCase: GetRequestResultUseCase,
    private val confirmFulfillmentUseCase: ConfirmFulfillmentUseCase,
    private val determineOrderNextActionUseCase: DetermineOrderNextActionUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OrderReviewState())
    val state = _state.asStateFlow()
    private val _effect = Channel<OrderReviewUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var requestId: Long? = null
    private var masterOrderId: Long? = null
    private var initialized = false
    private var fulfillmentConfirmationStarted = false

    fun onIntent(intent: OrderReviewUIIntent) {
        when (intent) {

            is OrderReviewUIIntent.Load -> {
                initialize(
                    intent.requestId,
                    intent.masterOrderId,
                    intent.selectedItems,
                )
            }

            is OrderReviewUIIntent.FulfillmentChanged -> {
                if (_state.value.nextAction == OrderNextAction.CHOOSE_FULFILLMENT) {
                    _state.update {
                        it.copy(
                            selectedFulfillment = intent.method,
                        )
                    }
                }
            }

            is OrderReviewUIIntent.PharmacyClicked -> {
                sendEffect(
                    OrderReviewUIEffect.NavigateToPharmacyProfile(
                        intent.pharmacyId,
                    )
                )
            }

            OrderReviewUIIntent.ConfirmFulfillment -> {
                confirmFulfillment()
            }

            OrderReviewUIIntent.PayClicked -> {
                pay()
            }

            OrderReviewUIIntent.Retry -> {
                loadOrder(isRefreshing = false)
            }

            OrderReviewUIIntent.Refresh -> {
                if (_state.value.order != null) {
                    loadOrder(isRefreshing = true)
                }
            }

            OrderReviewUIIntent.NavigateBack -> {
                sendEffect(OrderReviewUIEffect.NavigateBack)
            }
        }
    }

    private fun initialize(
        requestId: Long,
        masterOrderId: Long,
        selectedItems: List<SelectedOfferItem>,
    ) {
        if (initialized && this.masterOrderId == masterOrderId) return
        initialized = true
        this.requestId = requestId
        this.masterOrderId = masterOrderId

        _state.update {
            it.copy(
                selectedItems = selectedItems,
            )
        }

        viewModelScope.launch {

            loadOrderInternal(isRefreshing = false)

            when (val request = getMedicineRequestByIdUseCase(requestId)) {
                is MedsyResult.Success -> {
                    _state.update {
                        it.copy(
                            request = request.data,
                        )
                    }
                }

                is MedsyResult.Error -> Unit
            }

            when (val result = getRequestResultUseCase(requestId)) {
                is MedsyResult.Success -> {
                    _state.update {
                        it.copy(
                            requestResult = result.data,
                        )
                    }
                }

                is MedsyResult.Error -> Unit
            }
        }
    }

    private fun loadOrder(isRefreshing: Boolean) {
        if (masterOrderId == null) return

        viewModelScope.launch {
            loadOrderInternal(isRefreshing)
        }
    }

    private suspend fun loadOrderInternal(
        isRefreshing: Boolean,
    ): Boolean {

        val id = masterOrderId ?: return false

        _state.update {
            it.copy(
                isLoading = !isRefreshing,
                isRefreshing = isRefreshing,
                errorMessageRes = null,
            )
        }
        return when (val result = getOrderByIdUseCase(id)) {
            is MedsyResult.Success -> {
                val order = result.data
                val nextAction = determineOrderNextActionUseCase(order)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        order = order,
                        selectedFulfillment = order.fulfillmentMethod ?: it.selectedFulfillment,
                        nextAction = nextAction,
                        errorMessageRes = null,
                    )
                }
                if (nextAction == OrderNextAction.VIEW_DETAILS) {
                    sendEffect(OrderReviewUIEffect.NavigateToOrderDetails(order.id))
                }
                true
            }

            is MedsyResult.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessageRes = result.error.toMessageRes(),
                    )
                }
                false
            }
        }
    }

    private fun confirmFulfillment() {
        val requestId = requestId ?: return

        val method =
            _state.value.selectedFulfillment ?: return

        if (
            fulfillmentConfirmationStarted ||
            _state.value.fulfillmentConfirmed
        ) {
            return
        }

        fulfillmentConfirmationStarted = true

        viewModelScope.launch {

            _state.update {
                it.copy(
                    isConfirming = true,
                    errorMessageRes = null,
                )
            }

            when (
                val result =
                    confirmFulfillmentUseCase(
                        requestId,
                        method,
                    )
            ) {

                is MedsyResult.Success -> {

                    _state.update {
                        it.copy(
                            isConfirming = false,
                            fulfillmentConfirmed = true,
                        )
                    }

                    if (
                        result.data.paymentMethod ==
                        PaymentMethod.CASH
                    ) {

                        sendEffect(
                            OrderReviewUIEffect.NavigateToOrderDetails(
                                result.data.masterOrderId,
                            )
                        )

                    } else {

                        if (
                            !loadOrderInternal(
                                isRefreshing = false,
                            )
                        ) {
                            _state.value.errorMessageRes
                                ?.let { messageRes ->
                                    sendEffect(
                                        OrderReviewUIEffect.ShowError(
                                            messageRes,
                                        )
                                    )
                                }
                        }
                    }
                }

                is MedsyResult.Error -> {

                    fulfillmentConfirmationStarted = false

                    _state.update {
                        it.copy(
                            isConfirming = false,
                            errorMessageRes =
                                result.error.toMessageRes(),
                        )
                    }

                    sendEffect(
                        OrderReviewUIEffect.ShowError(
                            result.error.toMessageRes(),
                        )
                    )
                }
            }
        }
    }


    private fun pay() {

        if (
            _state.value.nextAction !=
            OrderNextAction.PAY_CARD
        ) {
            return
        }

        val orderId = masterOrderId ?: return

        sendEffect(
            OrderReviewUIEffect.StartCardPayment(
                orderId = orderId,
            )
        )
    }

    private fun sendEffect(
        effect: OrderReviewUIEffect,
    ) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}