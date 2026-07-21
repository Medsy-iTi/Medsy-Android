package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.orders.usecase.GetOrdersUseCase
import com.medsy.presentation.orders.model.OrderProductThumbnail
import com.medsy.presentation.orders.model.OrderStatus
import com.medsy.presentation.orders.model.OrderSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
            _state.update { it.copy(isLoading = true) }

            try {
                val domainOrders = getOrdersUseCase()

                val uiOrders = domainOrders.map { domainOrder ->

                    val presentationStatus = when (domainOrder.status) {
                        com.medsy.domain.orders.model.OrderStatus.Confirmed -> OrderStatus.Confirmed
                        com.medsy.domain.orders.model.OrderStatus.Delivered -> OrderStatus.Delivered
                        com.medsy.domain.orders.model.OrderStatus.Cancelled -> OrderStatus.Cancelled
                        com.medsy.domain.orders.model.OrderStatus.Pending -> OrderStatus.Confirmed
                    }

                    OrderSummary(
                        id = domainOrder.id,
                        dateLabel = domainOrder.dateLabel,
                        status = presentationStatus,
                        pharmacyName = domainOrder.pharmacyName,
                        total = domainOrder.total,
                        productCount = domainOrder.productCount,
                        productThumbnails = domainOrder.productThumbnails.map { thumb ->
                            OrderProductThumbnail(
                                productId = thumb.productId,
                                imageUrl = thumb.imageUrl
                            )
                        }
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        orders = uiOrders
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
