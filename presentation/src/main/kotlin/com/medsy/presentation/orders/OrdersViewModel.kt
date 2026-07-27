package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.fold
import com.medsy.domain.orders.model.OrderStatusDomain
import com.medsy.domain.orders.usecase.GetOrdersUseCase
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.orders.model.OrderProductThumbnail
import com.medsy.presentation.orders.model.OrderStatus
import com.medsy.presentation.orders.model.OrderSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(OrdersUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadOrders()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrdersUIState(),
        )

    private val _effect = Channel<OrdersUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: OrdersUIIntent) {
        when (intent) {
            is OrdersUIIntent.FilterSelected -> _state.update {
                it.copy(selectedFilter = intent.filter)
            }

            is OrdersUIIntent.OrderClicked ->
                sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = getOrdersUseCase(page = 0, size = 10, sort = null)

            result.fold(
                onSuccess = { pageDomain ->
                    val uiOrders = pageDomain.content.map { domainOrder ->

                        val presentationStatus = when (domainOrder.status) {
                            OrderStatusDomain.Confirmed -> OrderStatus.Confirmed
                            OrderStatusDomain.Delivered -> OrderStatus.Delivered
                            OrderStatusDomain.Cancelled -> OrderStatus.Cancelled
                            OrderStatusDomain.Pending -> OrderStatus.Confirmed
                        }

                        OrderSummary(
                            id = domainOrder.id.toString(),
                            dateLabel = domainOrder.dateLabel,
                            status = presentationStatus,
                            pharmacyName = domainOrder.pharmacyName,
                            total = domainOrder.total.toInt(),
                            productCount = domainOrder.items.sumOf { it.quantity },
                            productThumbnails = domainOrder.items.map { item ->
                                OrderProductThumbnail(
                                    productId = item.productId,
                                    imageUrl = item.imageUrl
                                )
                            }
                        )
                    }.reversed()

                    _state.update {
                        it.copy(
                            isLoading = false,
                            orders = uiOrders,
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

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
