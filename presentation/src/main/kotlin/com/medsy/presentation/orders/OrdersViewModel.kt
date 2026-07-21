package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class OrdersViewModel @Inject constructor() : ViewModel() {

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

    // TODO: replace with a real GetOrdersUseCase once the orders endpoint exists.
    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(300)

            val orders = listOf(
                OrderSummary(
                    id = "1258",
                    dateLabel = "Today",
                    status = OrderStatus.Confirmed,
                    pharmacyName = "Al Rahma Pharmacy",
                    total = 180,
                    productCount = 3,
                    productThumbnails = listOf(
                        OrderProductThumbnail("1", "https://example.com/images/product_1.png"),
                        OrderProductThumbnail("2", "https://example.com/images/panadol.png"),
                        OrderProductThumbnail("3", "https://example.com/images/panadol.png"),
                    ),
                ),
                OrderSummary(
                    id = "1230",
                    dateLabel = "Yesterday",
                    status = OrderStatus.Delivered,
                    pharmacyName = "Al Shifa Pharmacy",
                    total = 125,
                    productCount = 2,
                    productThumbnails = listOf(
                        OrderProductThumbnail("4", "https://example.com/images/panadol.png"),
                        OrderProductThumbnail("5", "https://example.com/images/panadol.png"),
                    ),
                ),
                OrderSummary(
                    id = "1205",
                    dateLabel = "May 12",
                    status = OrderStatus.Delivered,
                    pharmacyName = "El Ezaby Pharmacy",
                    total = 240,
                    productCount = 4,
                    productThumbnails = listOf(
                        OrderProductThumbnail("6", "https://example.com/images/product_2.png"),
                        OrderProductThumbnail("7", "https://example.com/images/panadol.png"),
                        OrderProductThumbnail("8", "https://example.com/images/panadol.png"),
                    ),
                ),
                OrderSummary(
                    id = "1180",
                    dateLabel = "May 9",
                    status = OrderStatus.Cancelled,
                    pharmacyName = null,
                    total = 0,
                    productCount = 2,
                    productThumbnails = listOf(
                        OrderProductThumbnail("9", "https://example.com/images/product_3.png"),
                        OrderProductThumbnail("10", "https://example.com/images/product_4.png"),
                    ),
                ),
            )

            _state.update { it.copy(isLoading = false, orders = orders) }
        }
    }

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
