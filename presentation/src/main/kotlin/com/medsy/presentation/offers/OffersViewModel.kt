package com.medsy.presentation.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.offers.model.OfferType
import com.medsy.domain.offers.usecase.AcceptOfferUseCase
import com.medsy.domain.offers.usecase.GetOffersForRequestUseCase
import com.medsy.domain.requests.repository.ActiveRequestRepository
import com.medsy.domain.requests.usecase.GetMedicineRequestByIdUseCase
import com.medsy.domain.productdetails.usecase.GetProductDetailsUseCase
import com.medsy.presentation.offers.model.OfferMedicine
import com.medsy.presentation.offers.model.PharmacyOffer
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val getMedicineRequestByIdUseCase: GetMedicineRequestByIdUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val acceptOfferUseCase: AcceptOfferUseCase,
    private val activeRequestRepository: ActiveRequestRepository
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
            OffersUIIntent.ConfirmOrder -> confirmOrder()
            OffersUIIntent.TrackOrder -> sendEffect(OffersUIEffect.NavigateToTrackOrder)
            OffersUIIntent.BackToHome -> sendEffect(OffersUIEffect.NavigateToHome)
            OffersUIIntent.NavigateBack -> sendEffect(OffersUIEffect.NavigateBack)
        }
    }

    private fun confirmOrder() {
        val selectedOffer = _state.value.selectedOffer ?: return
        val requestId = _state.value.requestId ?: return
        val selectedItemIds = selectedOffer.medicines.filter { it.isAvailable }.mapNotNull { it.id.toLongOrNull() }

        viewModelScope.launch {
            _state.update { it.copy(isConfirmingOrder = true) }
            val result = acceptOfferUseCase(requestId, selectedItemIds)
            when (result) {
                is MedsyResult.Success -> {
                    activeRequestRepository.removeActiveRequest(requestId)
                    val firstOrder = result.data.orders.firstOrNull()
                    val orderIdStr = "#MS-${selectedOffer.id}"
                    val pharmacyName = firstOrder?.pharmacyName ?: _state.value.selectedOffer?.pharmacyName.orEmpty()
                    _state.update { 
                        it.copy(
                            isConfirmingOrder = false, 
                            orderConfirmed = true,
                            orderId = orderIdStr
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

    private fun loadOffers(requestId: Long, offerIdToSelect: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, requestId = requestId) }
            val requestDetailsResult = getMedicineRequestByIdUseCase(requestId)
            val offersResult = getOffersForRequestUseCase(requestId)

            if (requestDetailsResult is MedsyResult.Success && offersResult is MedsyResult.Success) {
                val requestDetails = requestDetailsResult.data
                val originalItems = requestDetails.items
                
                val pharmacyOffers = offersResult.data.content.map { offer ->
                    async {
                        val offerMedicines = originalItems.map { reqItem ->
                            async {
                                val offerItem = offer.items.find { it.requestItemId == reqItem.id }
                                val isAvailable = offerItem != null
                                
                                var finalName = reqItem.productName
                                var finalPrice = reqItem.unitPrice.toInt()
                                var finalImage = reqItem.imageUrl
                                var finalPackage = reqItem.packSize ?: ""
                                var isSubstitute = false
                                var originalName: String? = null
                                
                                if (isAvailable && offerItem.productId != reqItem.productId) {
                                    isSubstitute = true
                                    originalName = reqItem.productName
                                    finalImage = null
                                    val prodResult = getProductDetailsUseCase(offerItem.productId, "en")
                                    if (prodResult is MedsyResult.Success) {
                                        finalName = prodResult.data.name
                                        finalPrice = prodResult.data.price.toInt()
                                        finalImage = prodResult.data.imageUrl
                                        finalPackage = prodResult.data.packSize ?: ""
                                    }
                                }
                                
                                OfferMedicine(
                                    id = reqItem.id.toString(),
                                    name = finalName,
                                    packageInfo = finalPackage,
                                    price = finalPrice,
                                    isAvailable = isAvailable,
                                    quantity = reqItem.quantity,
                                    imageUrl = finalImage,
                                    isSubstitute = isSubstitute,
                                    originalProductName = originalName
                                )
                            }
                        }.awaitAll()

                        val availableCount = offerMedicines.count { it.isAvailable }
                    val type = if (availableCount == offerMedicines.size) OfferType.FULL else OfferType.PARTIAL
                    val totalPrice = offerMedicines.filter { it.isAvailable }.sumOf { it.price * it.quantity }
                    
                    PharmacyOffer(
                        id = offer.id.toString(),
                        pharmacyName = offer.pharmacyName ?:"",
                        managerName = offer.pharmacistName ?:"",
                        price = totalPrice,
                        type = type,
                        medicines = offerMedicines,
                        pharmacistComment = null
                    )
                    }
                }.awaitAll()
                
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
