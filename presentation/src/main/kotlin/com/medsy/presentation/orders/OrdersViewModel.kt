package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.fold
import com.medsy.domain.orders.model.OrderStatusDomain
import com.medsy.domain.orders.usecase.GetOrdersUseCase
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.common.util.formatOrderDate
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
    private val allFetchedOrders = mutableListOf<OrderSummary>()

    private val _state = MutableStateFlow(OrdersUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                reloadOrders()
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

            OrdersUIIntent.LoadNextPage -> {
                loadNextPage()
            }

            OrdersUIIntent.Retry -> {
                reloadOrders()
            }

            OrdersUIIntent.Refresh -> {
                reloadOrders(isPullToRefresh = true)
            }
        }
    }

    private fun reloadOrders(isPullToRefresh: Boolean = false) {
        _state.update {
            it.copy(
                isLoading = !isPullToRefresh,
                isRefreshing = isPullToRefresh,
                currentPage = 0,
                errorMessageRes = null
            )
        }
        allFetchedOrders.clear()
        fetchPage(0)
    }

    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.isLoadMore || currentState.isLastPage) return

        android.util.Log.d("OrdersViewModel", "Loading next page of orders: ${currentState.currentPage + 1}")
        _state.update { it.copy(isLoadMore = true) }
        fetchPage(currentState.currentPage + 1)
    }

    private fun fetchPage(page: Int, sort: List<String>? = listOf("id,desc")) {
        viewModelScope.launch {
            val result = getOrdersUseCase(
                page = page,
                size = 10,
                sort = sort
            )

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
                            dateLabel = formatOrderDate(domainOrder.dateLabel),
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
                    }

                    if (page == 0) {
                        allFetchedOrders.clear()
                    }
                    allFetchedOrders.addAll(uiOrders)

                    android.util.Log.d("OrdersViewModel", "Loaded ${uiOrders.size} orders for page $page. Total loaded: ${allFetchedOrders.size}")

                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadMore = false,
                            currentPage = pageDomain.pageNumber,
                            totalPages = pageDomain.totalPages,
                            isLastPage = pageDomain.last,
                            orders = allFetchedOrders.toList(),
                            errorMessageRes = null
                        )
                    }
                },
                onError = { error ->
                    android.util.Log.e("OrdersViewModel", "Error loading page $page: $error")
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadMore = false,
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
