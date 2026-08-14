package com.medsy.presentation.orders.orderslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.usecase.GetOrdersUseCase
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.usecase.DetermineOrderNextActionUseCase
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val determineOrderNextActionUseCase: DetermineOrderNextActionUseCase,
) : ViewModel() {
    private var hasLoadedInitialData = false
    private var hasHandledInitialResume = false
    private val allFetchedOrders = mutableListOf<MasterOrder>()

    private val _state = MutableStateFlow(OrdersUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                hasLoadedInitialData = true
                reloadOrders()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OrdersUIState())

    private val _effect = Channel<OrdersUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: OrdersUIIntent) {
        when (intent) {
            is OrdersUIIntent.FilterSelected -> _state.update { it.copy(selectedFilter = intent.filter) }
            is OrdersUIIntent.OrderClicked -> {
                val order = allFetchedOrders.firstOrNull { it.id == intent.orderId } ?: return
                when (determineOrderNextActionUseCase(order)) {
                    OrderNextAction.CHOOSE_FULFILLMENT,
                    OrderNextAction.PAY_CARD -> sendEffect(
                        OrdersUIEffect.NavigateToOrderReview(order.requestId, order.id)
                    )
                    OrderNextAction.VIEW_DETAILS -> sendEffect(
                        OrdersUIEffect.NavigateToOrderDetails(order.id.toString())
                    )
                }
            }
            OrdersUIIntent.LoadNextPage -> loadNextPage()
            OrdersUIIntent.Retry -> reloadOrders()
            OrdersUIIntent.Refresh -> reloadOrders(isPullToRefresh = true)
            OrdersUIIntent.Resume -> {
                if (hasHandledInitialResume) reloadOrders() else hasHandledInitialResume = true
            }
        }
    }

    private fun reloadOrders(isPullToRefresh: Boolean = false) {
        _state.update {
            it.copy(
                isLoading = !isPullToRefresh,
                isRefreshing = isPullToRefresh,
                currentPage = 0,
                errorMessageRes = null,
            )
        }
        allFetchedOrders.clear()
        fetchPage(0)
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadMore || current.isLastPage) return
        _state.update { it.copy(isLoadMore = true) }
        fetchPage(current.currentPage + 1)
    }

    private fun fetchPage(page: Int) {
        viewModelScope.launch {
            getOrdersUseCase(page = page, size = 10, sort = listOf("id,desc"))
                .onSuccess { resultPage ->
                    if (page == 0) allFetchedOrders.clear()
                    allFetchedOrders.addAll(resultPage.content)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadMore = false,
                            currentPage = resultPage.pageNumber,
                            totalPages = resultPage.totalPages,
                            isLastPage = resultPage.last,
                            orders = allFetchedOrders.toList(),
                            errorMessageRes = null,
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadMore = false,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
