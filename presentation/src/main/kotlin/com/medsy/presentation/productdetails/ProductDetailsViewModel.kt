package com.medsy.presentation.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.productdetails.model.ProductDetails
import com.medsy.domain.productdetails.usecase.GetProductDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.productdetails.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) : ViewModel() {

    private var productId: Int = -1
    private var hasLoadedInitialData = false

    /** Called once from the nav entry, before the ViewModel is observed. */
    fun init(rawId: String) {
        productId = rawId.toIntOrNull() ?: -1
        if (!hasLoadedInitialData) {
            loadProduct()
            hasLoadedInitialData = true
        }
    }

    private val _state = MutableStateFlow(ProductDetailsUIState())
    val state = _state
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
            ProductDetailsUIIntent.RetryClicked -> loadProduct()
        }
    }

    private fun loadProduct() {
        if (productId < 0) {
            _state.update { it.copy(isLoading = false, errorMessage = "Invalid product ID") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val language = if (Locale.getDefault().language == "ar") "ar" else "en"

            val result = getProductDetailsUseCase(productId, language)

            when (result) {
                is MedsyResult.Success -> {
                    val details = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            product = details.toUiModel(),
                            isFavorite = false,
                            errorMessage = null
                        )
                    }
                }
                is MedsyResult.Error -> {
                val errorResId = when (result.error) {
                    is MedsyError.Remote.NoInternet -> R.string.error_no_internet
                    is MedsyError.Remote.Http -> R.string.error_server_message
                    else -> R.string.error_unknown
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorResId.toString()
                    )
                }
            }
            }
        }
    }

    private fun toggleFavorite() {
        _state.update { it.copy(isFavorite = !it.isFavorite) }
    }

    private fun addToCart() {
        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }
            delay(400)
            _state.update { it.copy(isAddingToCart = false) }
            sendEffect(ProductDetailsUIEffect.NavigateToCart)
        }
    }

    private fun sendEffect(effect: ProductDetailsUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun ProductDetails.toUiModel(): Product {
        return Product(
            id = id.toString(),
            name = name,
            imageUrls = if (imageUrl.isNotBlank()) listOf(imageUrl) else emptyList(),
            strength = scientificName,
            packInfo = categoryName,
            price = price.toInt(),
            description = "",
            manufacturer = company,
            type = categoryName,
            category = categoryName,
            route = route,
            isFavorite = false
        )
    }
}
