package com.medsy.presentation.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.domain.offers.usecase.AcceptOfferUseCase
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.requests.usecase.RemoveActiveRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


import com.medsy.domain.offers.usecase.StreamRequestResultUseCase
import kotlinx.coroutines.flow.catch

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val streamRequestResultUseCase: StreamRequestResultUseCase,
    private val acceptOfferUseCase: AcceptOfferUseCase,
    private val removeActiveRequestUseCase: RemoveActiveRequestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OffersState())
    val state: StateFlow<OffersState> = _state.asStateFlow()

    private val _effect = Channel<OffersUIEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: OffersUIIntent) {
        when (intent) {
            is OffersUIIntent.LoadOffers -> loadOffers(intent.requestId)
            is OffersUIIntent.ToggleItemSelection -> {
                val currentSet = _state.value.selectedItemIds
                val newSet = if (currentSet.contains(intent.requestItemId)) {
                    currentSet - intent.requestItemId
                } else {
                    currentSet + intent.requestItemId
                }
                _state.update { it.copy(selectedItemIds = newSet) }
            }
            is OffersUIIntent.SetSelectedItems -> {
                _state.update { it.copy(selectedItemIds = intent.itemIds) }
            }
            OffersUIIntent.ProceedToReview -> {
                sendEffect(OffersUIEffect.NavigateToOrderReview(_state.value.selectedItemIds))
            }
            OffersUIIntent.ConfirmOrder -> confirmOrder()
            OffersUIIntent.TrackOrder -> sendEffect(OffersUIEffect.NavigateToTrackOrder)
            OffersUIIntent.BackToHome -> sendEffect(OffersUIEffect.NavigateToHome)
            OffersUIIntent.NavigateBack -> sendEffect(OffersUIEffect.NavigateBack)
        }
    }

    private fun confirmOrder() {
        val requestResult = _state.value.requestResult ?: return
        val requestId = _state.value.requestId ?: return
        val selectedItemIds = _state.value.selectedItemIds

        if (selectedItemIds.isEmpty()) return

        val selectedItems = requestResult.items
            .filter { selectedItemIds.contains(it.requestItemId) && it.productId != null }
            .mapNotNull { item ->
                item.productId?.let { SelectedOfferItem(item.requestItemId, it) }
            }

        viewModelScope.launch {
            _state.update { it.copy(isConfirmingOrder = true) }
            val result = acceptOfferUseCase(requestId, selectedItems)
            when (result) {
                is MedsyResult.Success -> {
                    removeActiveRequestUseCase(requestId)
                    val firstOrder = result.data.orders.firstOrNull()
                    val orderIdStr = "#MS-${firstOrder?.orderId ?: requestId}"
                    val pharmacyName = firstOrder?.pharmacyName.orEmpty()
                    _state.update {
                        it.copy(
                            isConfirmingOrder = false,
                            orderConfirmed = true,
                            orderId = orderIdStr,
                            pharmacyName = pharmacyName
                        )
                    }
                    sendEffect(OffersUIEffect.NavigateToOrderConfirmation(
                        orderIdStr,
                        pharmacyName
                    ))
                }
                is MedsyResult.Error -> {
                    _state.update { it.copy(isConfirmingOrder = false) }
                    sendEffect(OffersUIEffect.ShowError(result.error.toMessageRes()))
                }
            }
        }
    }

    private fun loadOffers(requestId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, requestId = requestId) }
            
            streamRequestResultUseCase(requestId)
                .catch { _ ->
                    _state.update { it.copy(isLoading = false) }
                }
                .collect { result ->
                    val allAvailableIds = result.items.filter { it.isAvailable }.map { it.requestItemId }.toSet()
                    
                    _state.update {
                        val initialSelected = if (it.selectedItemIds.isEmpty() && it.requestResult == null) allAvailableIds else it.selectedItemIds
                        val validSelected = initialSelected.intersect(allAvailableIds)
                        
                        it.copy(
                            isLoading = false,
                            requestResult = result,
                            selectedItemIds = validSelected
                        )
                    }
                }
        }
    }

    private fun sendEffect(effect: OffersUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
