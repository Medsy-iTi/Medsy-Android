package com.medsy.presentation.orders.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.fold
import com.medsy.domain.orders.model.OrderStatusDomain
import com.medsy.domain.orders.usecase.GetOrderByIdUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.orders.details.model.FulfillmentType
import com.medsy.presentation.orders.details.model.OrderDetails
import com.medsy.presentation.orders.details.model.OrderLineItem
import com.medsy.presentation.orders.details.model.OrderPharmacyInfo
import com.medsy.presentation.orders.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {

    private var orderId: String = ""
    private var hasLoadedInitialData = false

    fun init(id: String) {
        if (orderId != id) {
            orderId = id
            loadOrderDetails()
        }
    }

    private val _state = MutableStateFlow(OrderDetailsUIState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrderDetailsUIState(),
        )

    private val _effect = Channel<OrderDetailsUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            OrderDetailsUIIntent.BackClicked ->
                sendEffect(OrderDetailsUIEffect.NavigateBack)

            OrderDetailsUIIntent.RetryClicked -> loadOrderDetails()

            OrderDetailsUIIntent.PharmacyClicked -> {
                _state.value.order?.pharmacy?.id?.let { pharmacyId ->
                    sendEffect(OrderDetailsUIEffect.NavigateToPharmacyProfile(pharmacyId))
                }
            }

            OrderDetailsUIIntent.ReorderClicked ->
                sendEffect(OrderDetailsUIEffect.ReorderRequested(orderId))
        }
    }

    private fun loadOrderDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }

            val idAsLong = orderId.toLongOrNull()
            if (idAsLong == null) {
                _state.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.order_details_error_load)
                }
                return@launch
            }

            val result = getOrderByIdUseCase(idAsLong)

            result.fold(
                onSuccess = { domainOrder ->
                    val presentationStatus = when (domainOrder.status) {
                        OrderStatusDomain.Confirmed -> OrderStatus.Confirmed
                        OrderStatusDomain.Delivered -> OrderStatus.Delivered
                        OrderStatusDomain.Cancelled -> OrderStatus.Cancelled
                        OrderStatusDomain.Pending -> OrderStatus.Confirmed
                    }

                    val orderDetails = OrderDetails(
                        id = domainOrder.id,
                        status = presentationStatus,
                        dateLabel = domainOrder.date,
                        fulfillmentType = FulfillmentType.Delivery,
                        pharmacy = domainOrder.pharmacyId?.let {
                            OrderPharmacyInfo(
                                id = it,
                                name = domainOrder.pharmacyName ?: "",
                                address = domainOrder.pharmacyAddress,
                                phone = domainOrder.pharmacyPhone
                            )
                        },
                        lineItems = domainOrder.items.map { item ->
                            OrderLineItem(
                                id = item.id,
                                medicineName = item.productName ?: "",
                                imageUrl = item.imageUrl,
                                quantity = item.quantity,
                                unitPrice = item.unitPrice,
                                alternativeToMedicineName = null,
                                productId = item.productId
                            )
                        },
                        itemsSubtotal = domainOrder.subTotal,
                        deliveryFee = domainOrder.deliveryFee,
                        finalTotal = domainOrder.total
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            order = orderDetails,
                            errorMessageRes = null
                        )
                    }
                },
                onError = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = error.toMessageRes()
                        )
                    }
                }
            )
        }
    }

    private fun sendEffect(effect: OrderDetailsUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

}
