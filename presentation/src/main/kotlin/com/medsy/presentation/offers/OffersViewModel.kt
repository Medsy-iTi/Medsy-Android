package com.medsy.presentation.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.offers.model.OfferMedicine
import com.medsy.presentation.offers.model.OfferType
import com.medsy.presentation.offers.model.PharmacyOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(OffersState())
    val state: StateFlow<OffersState> = _state.asStateFlow()

    private val _effect = Channel<OffersUIEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadMockOffers()
    }

    fun onIntent(intent: OffersUIIntent) {
        when (intent) {
            OffersUIIntent.RefreshOffers -> loadMockOffers()
            is OffersUIIntent.SelectOffer -> {
                val selected = _state.value.availableOffers.find { it.id == intent.offerId }
                _state.update { it.copy(selectedOffer = selected) }
                sendEffect(OffersUIEffect.NavigateToOfferDetails)
            }
            OffersUIIntent.ChooseSelectedOffer -> {
                sendEffect(OffersUIEffect.NavigateToOrderReview)
            }
            OffersUIIntent.ConfirmOrder -> {
                viewModelScope.launch {
                    _state.update { it.copy(isConfirmingOrder = true) }
                    delay(1500) // Simulate network call
                    _state.update { 
                        it.copy(
                            isConfirmingOrder = false, 
                            orderConfirmed = true,
                            orderId = "#MS-250721-001"
                        ) 
                    }
                    sendEffect(OffersUIEffect.NavigateToOrderConfirmation)
                }
            }
            OffersUIIntent.TrackOrder -> sendEffect(OffersUIEffect.NavigateToTrackOrder)
            OffersUIIntent.BackToHome -> sendEffect(OffersUIEffect.NavigateToHome)
            OffersUIIntent.NavigateBack -> sendEffect(OffersUIEffect.NavigateBack)
        }
    }

    private fun loadMockOffers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000)
            
            val mockMedicines = listOf(
                OfferMedicine(
                    id = "1",
                    name = "بانادول اكسترا",
                    packageInfo = "20 قرص",
                    price = 24,
                    isAvailable = true
                ),
                OfferMedicine(
                    id = "2",
                    name = "أموكسيسيلين 500 مجم",
                    packageInfo = "16 كبسولة",
                    price = 12,
                    isAvailable = true
                ),
                OfferMedicine(
                    id = "3",
                    name = "بروفين 400 مجم",
                    packageInfo = "10 أقراص",
                    price = 8,
                    isAvailable = true
                ),
                OfferMedicine(
                    id = "4",
                    name = "فيتامين سي 1000 مجم",
                    packageInfo = "10 أقراص",
                    price = 4,
                    isAvailable = true
                )
            )

            val mockOffers = listOf(
                PharmacyOffer(
                    id = "1",
                    pharmacyName = "النهضة",
                    managerName = "محمد أحمد",
                    price = 48,
                    type = OfferType.FULL,
                    medicines = mockMedicines,
                    pharmacistComment = "مرحباً، جميع الأدوية متوفرة وجاهزة للتجهيز. يرجى الالتزام بالجرعات الموضحة. نتمنى لك الشفاء العاجل 💊"
                ),
                PharmacyOffer(
                    id = "2",
                    pharmacyName = "الشفاء",
                    managerName = "علي حسن",
                    price = 36,
                    type = OfferType.PARTIAL,
                    medicines = mockMedicines.mapIndexed { index, med -> 
                        med.copy(isAvailable = index != 3) // Make the last one unavailable
                    }
                ),
                PharmacyOffer(
                    id = "3",
                    pharmacyName = "من صيدليتين",
                    managerName = "متعدد",
                    price = 46,
                    type = OfferType.COMBINED,
                    medicines = mockMedicines
                )
            )
            
            _state.update { 
                it.copy(
                    isLoading = false,
                    availableOffers = mockOffers,
                    selectedOffer = it.selectedOffer ?: mockOffers.first()
                ) 
            }
        }
    }

    private fun sendEffect(effect: OffersUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
