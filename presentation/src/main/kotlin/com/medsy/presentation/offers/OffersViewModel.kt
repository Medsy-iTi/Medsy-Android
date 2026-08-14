package com.medsy.presentation.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.offers.model.RequestItemAvailability
import com.medsy.domain.offers.model.RequestItemUpdate
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultEvent
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.offers.usecase.AcceptOfferUseCase
import com.medsy.domain.offers.usecase.GetRequestResultUseCase
import com.medsy.domain.offers.usecase.StreamRequestResultUseCase
import com.medsy.domain.orders.usecase.GetOrderByRequestIdUseCase
import com.medsy.domain.requests.model.MedicineRequestStatus
import com.medsy.domain.requests.usecase.GetMedicineRequestByIdUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val getMedicineRequestByIdUseCase: GetMedicineRequestByIdUseCase,
    private val getRequestResultUseCase: GetRequestResultUseCase,
    private val streamRequestResultUseCase: StreamRequestResultUseCase,
    private val acceptOfferUseCase: AcceptOfferUseCase,
    private val getOrderByRequestIdUseCase: GetOrderByRequestIdUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OffersState())
    val state = _state.asStateFlow()

    private val _effect = Channel<OffersUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var streamJob: Job? = null
    private var lastRequestId: Long? = null
    private var selectionSubmissionStarted = false
    private var orderResolutionStarted = false

    fun onIntent(intent: OffersUIIntent) {
        when (intent) {
            is OffersUIIntent.LoadOfferDetails -> loadOfferDetails(intent.requestId)
            is OffersUIIntent.SelectProduct -> selectProduct(intent.requestItemId, intent.productId)
            OffersUIIntent.ProceedToReview -> submitSelection()
            OffersUIIntent.Retry -> _state.value.requestId?.let { requestId ->
                if (_state.value.selectionSubmitted) resolveCreatedOrder(requestId)
                else loadOfferDetails(requestId, force = true)
            }

            OffersUIIntent.NavigateBack -> sendEffect(OffersUIEffect.NavigateBack)
        }
    }

    private fun loadOfferDetails(requestId: Long, force: Boolean = false) {
        if (!force && lastRequestId == requestId) return
        lastRequestId = requestId
        streamJob?.cancel()
        viewModelScope.launch {
            _state.value = OffersState(requestId = requestId, isLoading = true)
            when (val existingOrder = getOrderByRequestIdUseCase(requestId)) {
                is MedsyResult.Success -> {
                    if (existingOrder.data.fulfillmentMethod == null) {
                        _state.update { it.copy(isLoading = false, selectionSubmitted = true) }
                        sendEffect(
                            OffersUIEffect.NavigateToOrderReview(
                                existingOrder.data.id,
                                emptyList(),
                            )
                        )
                        return@launch
                    }
                }

                is MedsyResult.Error -> Unit
            }
            when (val request = getMedicineRequestByIdUseCase(requestId)) {
                is MedsyResult.Success -> _state.update { it.copy(request = request.data) }
                is MedsyResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = request.error.toMessageRes()
                        )
                    }
                    return@launch
                }
            }
            if (_state.value.request?.status?.isSearchable() != true) {
                _state.update { it.copy(isLoading = false, selectionSubmitted = true) }
                sendEffect(OffersUIEffect.ShowError(R.string.offers_load_error))
                return@launch
            }
            when (val result = getRequestResultUseCase(requestId)) {
                is MedsyResult.Success -> _state.update {
                    it.withResultAndDefaultExactSelections(result.data)
                        .copy(isLoading = false, errorMessageRes = null)
                }

                is MedsyResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessageRes = result.error.toMessageRes())
                }
            }
            startStream(requestId)
        }
    }

    private fun startStream(requestId: Long) {
        streamJob = viewModelScope.launch {
            streamRequestResultUseCase(requestId).collect { result ->
                if (result !is MedsyResult.Success) return@collect
                when (val event = result.data) {
                    is RequestResultEvent.Snapshot -> _state.update {
                        it.withResultAndDefaultExactSelections(event.result).copy(isLoading = false)
                    }

                    is RequestResultEvent.ItemsUpdated -> _state.update { current ->
                        val updated = current.requestResult?.applyUpdates(event.items)
                            ?: return@update current
                        current.withResultAndDefaultExactSelections(updated)
                    }

                    is RequestResultEvent.Closed -> Unit
                }
            }
        }
    }

    private fun selectProduct(requestItemId: Long, productId: Long?) {
        if (_state.value.selectionSubmitted) return
        _state.update { current ->
            val remaining = current.selectedItems.filterNot { it.requestItemId == requestItemId }
            current.copy(
                selectedItems = if (productId == null) remaining else remaining + SelectedOfferItem(
                    requestItemId,
                    productId
                ),
                manuallyDeselectedRequestItemIds = if (productId == null) {
                    current.manuallyDeselectedRequestItemIds + requestItemId
                } else {
                    current.manuallyDeselectedRequestItemIds - requestItemId
                },
            )
        }
    }

    private fun submitSelection() {
        val current = _state.value
        val requestId = current.requestId ?: return
        if (current.selectedItems.isEmpty()) return
        if (current.selectionSubmitted) {
            resolveCreatedOrder(requestId)
            return
        }
        if (selectionSubmissionStarted) return
        selectionSubmissionStarted = true
        streamJob?.cancel()
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessageRes = null) }
            acceptOfferUseCase(requestId, current.selectedItems)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false, selectionSubmitted = true) }
                    resolveCreatedOrder(requestId)
                }
                .onError { error ->
                    selectionSubmissionStarted = false
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessageRes = error.toMessageRes()
                        )
                    }
                    sendEffect(OffersUIEffect.ShowError(error.toMessageRes()))
                }
        }
    }

    private fun resolveCreatedOrder(requestId: Long) {
        if (orderResolutionStarted) return
        orderResolutionStarted = true
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessageRes = null) }
            getOrderByRequestIdUseCase(requestId)
                .onSuccess { order ->
                    orderResolutionStarted = false
                    _state.update { it.copy(isSubmitting = false) }
                    sendEffect(
                        OffersUIEffect.NavigateToOrderReview(
                            order.id,
                            _state.value.selectedItems
                        )
                    )
                }
                .onError { error ->
                    orderResolutionStarted = false
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessageRes = error.toMessageRes()
                        )
                    }
                    sendEffect(OffersUIEffect.ShowError(error.toMessageRes()))
                }
        }
    }

    private fun sendEffect(effect: OffersUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

private fun OffersState.withResultAndDefaultExactSelections(result: RequestResult): OffersState {
    val selectedRequestItemIds =
        selectedItems.mapTo(mutableSetOf(), SelectedOfferItem::requestItemId)
    val defaults = result.items.mapNotNull { item ->
        if (
            item.requestItemId in selectedRequestItemIds ||
            item.requestItemId in manuallyDeselectedRequestItemIds ||
            !item.isAvailable ||
            item.isAlternative
        ) null else {
            val productId = item.productId
                ?: request?.items?.firstOrNull { it.id == item.requestItemId }?.productId
            productId?.let { SelectedOfferItem(item.requestItemId, it) }
        }
    }
    return copy(requestResult = result, selectedItems = selectedItems + defaults)
}

private fun RequestResult.applyUpdates(updates: List<RequestItemUpdate>): RequestResult = copy(
    items = items.map { item ->
        val update =
            updates.lastOrNull { it.requestItemId == item.requestItemId } ?: return@map item
        when (update.status) {
            RequestItemAvailability.FOUND -> item.copy(isAvailable = true, isAlternative = false)
            RequestItemAvailability.ALTERNATIVE_FOUND -> item.copy(
                isAvailable = true,
                isAlternative = true,
                product = update.product ?: item.product,
                alternatives = update.product
                    ?.takeUnless { product -> item.alternatives.any { it.id == product.id } }
                    ?.let { item.alternatives + it }
                    ?: item.alternatives,
            )

            RequestItemAvailability.NOT_FOUND -> item.copy(isAvailable = false)
            RequestItemAvailability.UNKNOWN -> item
        }
    }
)

private fun MedicineRequestStatus.isSearchable(): Boolean =
    this in setOf(
        MedicineRequestStatus.PENDING,
        MedicineRequestStatus.SEARCHING,
        MedicineRequestStatus.OFFERS_READY,
    )
