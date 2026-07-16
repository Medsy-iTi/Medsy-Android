package com.medsy.presentation.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.productdetails.model.Product
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
class ProductDetailsViewModel @Inject constructor() : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProductDetailsUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadProduct()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductDetailsUIState()
        )

    private val _effect = Channel<ProductDetailsUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ProductDetailsUIIntent) {
        when (intent) {
            ProductDetailsUIIntent.BackClicked -> sendEffect(ProductDetailsUIEffect.NavigateBack)
            ProductDetailsUIIntent.ShareClicked -> sendEffect(ProductDetailsUIEffect.OpenShareSheet)
            ProductDetailsUIIntent.FavoriteClicked -> toggleFavorite()
            is ProductDetailsUIIntent.ImagePageChanged -> _state.update {
                it.copy(selectedImageIndex = intent.index)
            }
            ProductDetailsUIIntent.AddToCartClicked -> addToCart()
            ProductDetailsUIIntent.ConsultPharmacistClicked ->
                sendEffect(ProductDetailsUIEffect.NavigateToPharmacistChat)
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(300) // simulated repository call

            val product = Product(
                id = "panadol-extra",
                name = "Panadol Extra",
                imageUrls = listOf(
                    "https://example.com/images/panadol_extra_1.png",
                    "https://example.com/images/panadol_extra_2.png",
                    "https://example.com/images/panadol_extra_3.png",
                    "https://example.com/images/panadol_extra_4.png",
                ),
                strength = "500 mg",
                packInfo = "24 Tablets",
                price = 68,
                description = "A pain reliever and fever reducer. Dosage and duration " +
                        "of use are determined by the pharmacist or physician according " +
                        "to your condition.",
                manufacturer = "GlaxoSmithKline",
                type = "Pain reliever and fever reducer",
                category = "Analgesics & Pain Medications",
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    product = product,
                    isFavorite = product.isFavorite,
                )
            }
        }
    }

    private fun toggleFavorite() {
        _state.update { it.copy(isFavorite = !it.isFavorite) }
    }

    private fun addToCart() {
        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }
            delay(400) // simulated add-to-cart call
            _state.update { it.copy(isAddingToCart = false) }
            sendEffect(ProductDetailsUIEffect.NavigateToCart)
        }
    }

    private fun sendEffect(effect: ProductDetailsUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
