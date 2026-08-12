package com.medsy.presentation.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.domain.offers.usecase.AcceptOfferUseCase
import com.medsy.domain.offers.usecase.GetOffersForRequestUseCase
import com.medsy.domain.offers.usecase.GetRequestResultUseCase
import com.medsy.domain.payment.usecase.CreatePaymentIntentUseCase
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.requests.usecase.RemoveActiveRequestUseCase
import com.medsy.presentation.offers.mapper.toPharmacyOffer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val getOffersForRequestUseCase: GetOffersForRequestUseCase,
    private val getRequestResultUseCase: GetRequestResultUseCase,
    private val acceptOfferUseCase: AcceptOfferUseCase,
    private val removeActiveRequestUseCase: RemoveActiveRequestUseCase,
    private val createPaymentIntentUseCase: CreatePaymentIntentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OffersState())
    val state: StateFlow<OffersState> = _state.asStateFlow()

    private val _effect = Channel<OffersUIEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: OffersUIIntent) {
        when (intent) {
            is OffersUIIntent.LoadOffers -> loadOffers(intent.requestId)
            is OffersUIIntent.LoadOfferDetails -> loadOffers(intent.requestId, intent.offerId)
            is OffersUIIntent.SelectOffer -> {
                val selected = _state.value.availableOffers.find { it.id == intent.offerId }
                _state.update { it.copy(selectedOffer = selected) }
                sendEffect(OffersUIEffect.NavigateToOfferDetails)
            }
            OffersUIIntent.ChooseSelectedOffer -> {
                sendEffect(OffersUIEffect.NavigateToOrderReview)
            }
            OffersUIIntent.ConfirmOrder -> handleConfirmOrder()
            OffersUIIntent.PaymentSuccess -> handlePaymentSuccess()
            is OffersUIIntent.PaymentFailed -> handlePaymentFailed(intent.error)
            OffersUIIntent.PaymentCanceled -> handlePaymentCanceled()
            OffersUIIntent.TrackOrder -> sendEffect(OffersUIEffect.NavigateToTrackOrder)
            OffersUIIntent.BackToHome -> sendEffect(OffersUIEffect.NavigateToHome)
            OffersUIIntent.NavigateBack -> sendEffect(OffersUIEffect.NavigateBack)
        }
    }

    private fun handleConfirmOrder() {
        val selectedOffer = _state.value.selectedOffer ?: return
        val requestId = _state.value.requestId ?: return
        val selectedItems = selectedOffer.medicines
            .filter { it.isAvailable }
            .mapNotNull { medicine ->
                medicine.id.toLongOrNull()?.let { requestItemId ->
                    SelectedOfferItem(
                        requestItemId = requestItemId,
                        productId = medicine.productId,
                    )
                }
            }
        val selectedItemIds =
            selectedOffer.medicines.filter { it.isAvailable }.mapNotNull { it.id.toLongOrNull() }

        viewModelScope.launch {
            _state.update { it.copy(isConfirmingOrder = true) }
            val result = acceptOfferUseCase(requestId, selectedItems)
            when (result) {
                is MedsyResult.Success -> {
                    val orderId = result.data.orders.firstOrNull()?.orderId
                    if (orderId != null) {
                        createPaymentIntent(orderId)
                    } else {
                        _state.update { it.copy(isConfirmingOrder = false) }
                        sendEffect(OffersUIEffect.ShowError(com.medsy.presentation.R.string.error_generic))
                    }
                }

                is MedsyResult.Error -> {
                    _state.update { it.copy(isConfirmingOrder = false) }
                    sendEffect(OffersUIEffect.ShowError(result.error.toMessageRes()))
                }
            }
        }
    }

    private fun createPaymentIntent(orderId: Long) {
        viewModelScope.launch {
            val result = createPaymentIntentUseCase(orderId)
            _state.update { it.copy(isConfirmingOrder = false) }
            when (result) {
                is MedsyResult.Success -> {
                    sendEffect(
                        OffersUIEffect.OpenPaymentSheet(
                            clientSecret = result.data.clientSecret
                        )
                    )
                }

                is MedsyResult.Error -> {
                    sendEffect(OffersUIEffect.ShowError(result.error.toMessageRes()))
                }
            }
        }
    }

    private fun handlePaymentSuccess() {
        val requestId = _state.value.requestId ?: return
        val selectedOffer = _state.value.selectedOffer ?: return
        val orderIdStr = "#MS-${selectedOffer.id}"
        val pharmacyName = selectedOffer.pharmacyName

        viewModelScope.launch {
            removeActiveRequestUseCase(requestId)
            _state.update {
                it.copy(
                    orderConfirmed = true,
                    orderId = orderIdStr
                )
            }
            sendEffect(
                OffersUIEffect.NavigateToOrderConfirmation(
                    orderIdStr,
                    pharmacyName
                )
            )
        }
    }

    private fun handlePaymentFailed(error: String?) {
        sendEffect(OffersUIEffect.ShowError(com.medsy.presentation.R.string.error_payment_failed))
    }

    private fun handlePaymentCanceled() {
    }

    private fun loadOffers(requestId: Long, offerIdToSelect: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, requestId = requestId) }

            val (requestResultResult, offersResult) = coroutineScope {
                val requestResultDeferred = async { getRequestResultUseCase(requestId) }
                val offersDeferred = async { getOffersForRequestUseCase(requestId) }
                Pair(requestResultDeferred.await(), offersDeferred.await())
            }

            if (requestResultResult is MedsyResult.Success && offersResult is MedsyResult.Success) {
                val resultItemMap = requestResultResult.data.items.associateBy { it.requestItemId }

                val pharmacyOffers = offersResult.data.content.map { offer ->
                    offer.toPharmacyOffer(resultItemMap)
                }

                _state.update {
                    val selected = if (offerIdToSelect != null) {
                        pharmacyOffers.find { offer -> offer.id == offerIdToSelect }
                    } else {
                        it.selectedOffer ?: pharmacyOffers.firstOrNull()
                    }
                    it.copy(
                        isLoading = false,
                        availableOffers = pharmacyOffers,
                        selectedOffer = selected,
                        requestId = requestId
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun sendEffect(effect: OffersUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

}
