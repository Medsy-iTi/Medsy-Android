package com.medsy.presentation.orders.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.R
import com.medsy.presentation.orders.details.model.FulfillmentType
import com.medsy.presentation.orders.details.model.OrderDetails
import com.medsy.presentation.orders.details.model.OrderLineItem
import com.medsy.presentation.orders.details.model.OrderPharmacyInfo
import com.medsy.presentation.orders.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OrderDetailsViewModel @Inject constructor() : ViewModel() {

    private var orderId: String = ""
    private var hasLoadedInitialData = false

    fun init(id: String) {
        orderId = id
        if (!hasLoadedInitialData) {
            loadOrderDetails()
            hasLoadedInitialData = true
        }
    }

    private val _state = MutableStateFlow(OrderDetailsUIState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrderDetailsUIState(),
        )

    private val _effect = Channel<OrderDetailsUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            OrderDetailsUIIntent.BackClicked ->
                sendEffect(OrderDetailsUIEffect.NavigateBack)

            OrderDetailsUIIntent.RetryClicked -> loadOrderDetails()

            OrderDetailsUIIntent.PharmacyClicked -> {
                _state.value.order?.pharmacy?.id?.let { pharmacyId ->
                    sendEffect(OrderDetailsUIEffect.NavigateToPharmacyProfile(pharmacyId))
                }
            }

            OrderDetailsUIIntent.ReorderClicked ->
                sendEffect(OrderDetailsUIEffect.ReorderRequested(orderId))
        }
    }

     private fun loadOrderDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
            delay(300)

            val order = mockOrderDetails[orderId]
            if (order == null) {
                _state.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.order_details_error_load)
                }
            } else {
                _state.update { it.copy(isLoading = false, order = order, errorMessageRes = null) }
            }
        }
    }

    private fun sendEffect(effect: OrderDetailsUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private companion object {
        val mockOrderDetails: Map<String, OrderDetails> = mapOf(
            "1258" to OrderDetails(
                id = "1258",
                status = OrderStatus.Confirmed,
                dateLabel = "Today",
                fulfillmentType = FulfillmentType.Delivery,
                pharmacy = OrderPharmacyInfo(id = "pharmacy-al-rahma", name = "Al Rahma Pharmacy"),
                lineItems = listOf(
                    OrderLineItem(
                        id = "1",
                        medicineName = "Panadol Extra 24 Tablets",
                        imageUrl = "https://example.com/images/panadol.png",
                        quantity = 2,
                        unitPrice = 45,
                    ),
                    OrderLineItem(
                        id = "2",
                        medicineName = "Curam 1g 14 Tablets",
                        imageUrl = "https://example.com/images/product_1.png",
                        quantity = 1,
                        unitPrice = 90,
                        alternativeToMedicineName = "Augmentin 1g 14 Tablets",
                    ),
                ),
                itemsSubtotal = 180,
                deliveryFee = 20,
                finalTotal = 200,
            ),
            "1230" to OrderDetails(
                id = "1230",
                status = OrderStatus.Delivered,
                dateLabel = "Yesterday",
                fulfillmentType = FulfillmentType.Pickup,
                pharmacy = OrderPharmacyInfo(id = "pharmacy-al-shifa", name = "Al Shifa Pharmacy"),
                lineItems = listOf(
                    OrderLineItem(
                        id = "3",
                        medicineName = "Vitamin C 1000mg 20 Effervescent Tablets",
                        imageUrl = "https://example.com/images/panadol.png",
                        quantity = 1,
                        unitPrice = 75,
                    ),
                    OrderLineItem(
                        id = "4",
                        medicineName = "Zinc 50mg 30 Tablets",
                        imageUrl = "https://example.com/images/panadol.png",
                        quantity = 1,
                        unitPrice = 50,
                    ),
                ),
                itemsSubtotal = 125,
                deliveryFee = null,
                finalTotal = 125,
            ),
            "1205" to OrderDetails(
                id = "1205",
                status = OrderStatus.Delivered,
                dateLabel = "May 12",
                fulfillmentType = FulfillmentType.Delivery,
                pharmacy = OrderPharmacyInfo(id = "pharmacy-el-ezaby", name = "El Ezaby Pharmacy"),
                lineItems = listOf(
                    OrderLineItem(
                        id = "5",
                        medicineName = "Cetal 500mg 20 Tablets",
                        imageUrl = "https://example.com/images/product_2.png",
                        quantity = 3,
                        unitPrice = 30,
                    ),
                    OrderLineItem(
                        id = "6",
                        medicineName = "Nasonex Nasal Spray",
                        imageUrl = "https://example.com/images/panadol.png",
                        quantity = 1,
                        unitPrice = 130,
                    ),
                    OrderLineItem(
                        id = "7",
                        medicineName = "Sterimar Nasal Spray",
                        imageUrl = "https://example.com/images/panadol.png",
                        quantity = 1,
                        unitPrice = 20,
                        alternativeToMedicineName = "Physiomer Nasal Spray",
                    ),
                ),
                itemsSubtotal = 220,
                deliveryFee = 20,
                finalTotal = 240,
            ),
            "1180" to OrderDetails(
                id = "1180",
                status = OrderStatus.Cancelled,
                dateLabel = "May 9",
                fulfillmentType = FulfillmentType.Delivery,
                pharmacy = null,
                lineItems = listOf(
                    OrderLineItem(
                        id = "8",
                        medicineName = "Betadine Antiseptic Solution",
                        imageUrl = "https://example.com/images/product_3.png",
                        quantity = 1,
                        unitPrice = 0,
                    ),
                    OrderLineItem(
                        id = "9",
                        medicineName = "Gauze Bandage Roll",
                        imageUrl = "https://example.com/images/product_4.png",
                        quantity = 1,
                        unitPrice = 0,
                    ),
                ),
                itemsSubtotal = 0,
                deliveryFee = null,
                finalTotal = 0,
            ),
        )
    }
}
