package com.medsy.presentation.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.productdetails.model.ProductDetails
import com.medsy.domain.productdetails.usecase.GetProductDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.productdetails.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addCartItem: AddCartItemUseCase,
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
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_invalid_id,
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
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
                            errorMessageRes = null,
                        )
                    }
                }
                is MedsyResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = result.error.toMessageRes(),
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
        if (_state.value.isAddingToCart) return
        val id = _state.value.product?.id?.toIntOrNull() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }
            addCartItem(id)
                .onSuccess {
                    _state.update { it.copy(isAddingToCart = false) }
                    sendEffect(
                        ProductDetailsUIEffect.ShowMessage(
                            R.string.product_details_added_to_cart
                        )
                    )
                }
                .onError { error ->
                    _state.update { it.copy(isAddingToCart = false) }
                    sendEffect(
                        ProductDetailsUIEffect.ShowMessage(error.toMessageRes())
                    )
                }
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
            imageUrls = imageUrl?.let { listOf(it) } ?: emptyList(),            strength = strength.orEmpty(),
            packInfo = packSize.orEmpty(),
            price = price.toInt(),
            description = description.orEmpty(),
            manufacturer = company,
            type = form.orEmpty(),
            category = consumerCategory.orEmpty(),
            route = route,
            isFavorite = false
        )
    }
}
