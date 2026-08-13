package com.medsy.presentation.orders.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.usecase.GetOrderByIdUseCase
import com.medsy.domain.orders.usecase.ReOrderUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
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
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val reOrderUseCase: ReOrderUseCase,
) : ViewModel() {
    private var orderId: Long? = null

    private val _state = MutableStateFlow(OrderDetailsUIState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        OrderDetailsUIState(),
    )

    private val _effect = Channel<OrderDetailsUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun init(id: String) {
        val parsed = id.toLongOrNull()
        if (parsed == null) {
            _state.update { it.copy(isLoading = false, errorMessageRes = R.string.order_details_error_load) }
        } else if (orderId != parsed) {
            orderId = parsed
            loadOrderDetails()
        }
    }

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            OrderDetailsUIIntent.BackClicked -> sendEffect(OrderDetailsUIEffect.NavigateBack)
            OrderDetailsUIIntent.RetryClicked -> loadOrderDetails()
            OrderDetailsUIIntent.Refresh -> loadOrderDetails(isPullToRefresh = true)
            is OrderDetailsUIIntent.PharmacyClicked ->
                sendEffect(OrderDetailsUIEffect.NavigateToPharmacyProfile(intent.pharmacyId))
            OrderDetailsUIIntent.ReorderClicked -> reorder()
            is OrderDetailsUIIntent.LineItemClicked ->
                sendEffect(OrderDetailsUIEffect.NavigateToProductDetails(intent.productId))
        }
    }

    private fun loadOrderDetails(isPullToRefresh: Boolean = false) {
        val id = orderId ?: return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = !isPullToRefresh,
                    isRefreshing = isPullToRefresh,
                    errorMessageRes = null,
                )
            }
            getOrderByIdUseCase(id)
                .onSuccess { order ->
                    _state.update {
                        it.copy(isLoading = false, isRefreshing = false, order = order)
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }

    private fun reorder() {
        val items = _state.value.order?.items.orEmpty().map {
            CartItemInput(productId = it.productId.toInt(), quantity = it.quantity)
        }
        if (items.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isReordering = true) }
            reOrderUseCase(items)
                .onSuccess {
                    _state.update { it.copy(isReordering = false) }
                    sendEffect(OrderDetailsUIEffect.ReorderRequested)
                }
                .onError { error ->
                    _state.update { it.copy(isReordering = false) }
                    sendEffect(OrderDetailsUIEffect.ShowErrorSnackbar(error.toMessageRes()))
                }
        }
    }

    private fun sendEffect(effect: OrderDetailsUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
