package com.medsy.presentation.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.LocaleConstants
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.favorites.model.FavoriteProduct
import com.medsy.domain.favorites.usecase.AddFavoriteUseCase
import com.medsy.domain.favorites.usecase.GetFavoritesUseCase
import com.medsy.domain.favorites.usecase.RemoveFavoriteUseCase
import com.medsy.domain.productdetails.model.ProductDetails
import com.medsy.domain.productdetails.usecase.GetProductDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.productdetails.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
) : ViewModel() {

    private var productId: Int = -1
    private var hasLoadedInitialData = false
    private var loadedDetails: ProductDetails? = null
    private var currentUserId: Long = 0L
    private var favoritesJob: Job? = null

    fun init(rawId: String) {
        productId = rawId.toIntOrNull() ?: -1
        if (!hasLoadedInitialData) {
            observeSessionAndFavorites()
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
            ProductDetailsUIIntent.CartClicked ->
                sendEffect(ProductDetailsUIEffect.NavigateToCart)

            ProductDetailsUIIntent.RetryClicked -> loadProduct()
        }
    }

    private fun observeSessionAndFavorites() {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                val newUserId = session?.user?.id ?: 0L
                if (newUserId != currentUserId) {
                    currentUserId = newUserId
                    observeFavoritesForUser(newUserId)
                }
            }
        }
    }

    private fun observeFavoritesForUser(userId: Long) {
        favoritesJob?.cancel()
        if (userId == 0L) {
            _state.update { it.copy(isFavorite = false) }
            return
        }
        favoritesJob = viewModelScope.launch {
            getFavoritesUseCase(userId).collect { result ->
                result.onSuccess { favorites ->
                    val isFav = favorites.any { it.id == productId }
                    _state.update { currentState ->
                        currentState.copy(isFavorite = isFav)
                    }
                }
            }
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

        if (_state.value.product?.id == productId.toString()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
            val language = if (Locale.getDefault().language == LocaleConstants.ARABIC_TAG) {
                LocaleConstants.ARABIC_TAG
            } else {
                LocaleConstants.ENGLISH_TAG
            }

            when (val result = getProductDetailsUseCase(productId, language)) {
                is MedsyResult.Success -> {
                    val details = result.data
                    loadedDetails = details
                    _state.update {
                        it.copy(
                            isLoading = false,
                            product = details.toUiModel(),
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
        val userId = currentUserId
        if (userId == 0L || productId < 0) return
        val details = loadedDetails ?: return
        val productName = details.name

        viewModelScope.launch {
            if (_state.value.isFavorite) {
                removeFavoriteUseCase(productId, userId)
                    .onSuccess {
                        sendEffect(
                            ProductDetailsUIEffect.ShowMessage(
                                R.string.search_removed_from_favorites,
                                listOf(productName),
                                isSuccess = true,
                            )
                        )
                    }
            } else {
                addFavoriteUseCase(details.toFavorite(), userId)
                    .onSuccess {
                        sendEffect(
                            ProductDetailsUIEffect.ShowMessage(
                                R.string.search_added_to_favorites,
                                listOf(productName),
                                isSuccess = true,
                            )
                        )
                    }
            }
        }
    }

    private fun addToCart() {
        if (_state.value.isAddingToCart) return
        val id = _state.value.product?.id?.toIntOrNull() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }
            addCartItem(id)
                .onSuccess { cart ->
                    _state.update { it.copy(isAddingToCart = false) }
                    val cartItem = cart.items.find { it.productId == id }
                    if (cartItem != null) {
                        sendEffect(
                            ProductDetailsUIEffect.ShowMessage(
                                R.string.product_added_to_cart_format,
                                args = listOf(cartItem.quantity, cartItem.productName),
                                isSuccess = true
                            )
                        )
                    } else {
                        sendEffect(
                            ProductDetailsUIEffect.ShowMessage(
                                R.string.product_details_added_to_cart,
                                isSuccess = true
                            )
                        )
                    }
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

    private fun ProductDetails.toFavorite(): FavoriteProduct {
        return FavoriteProduct(
            id = id,
            name = name,
            arabicName = name,
            scientificName = scientificName,
            price = price,
            imageUrl = imageUrl,
            categoryId = categoryId,
            categoryName = consumerCategory.orEmpty(),
            company = company,
            route = route
        )
    }

    private fun ProductDetails.toUiModel(): Product {
        return Product(
            id = id.toString(),
            name = name,
            imageUrls = imageUrl?.let { listOf(it) } ?: emptyList(),
            strength = strength.orEmpty(),
            packInfo = packSize.orEmpty(),
            price = price.toInt(),
            description = description.orEmpty(),
            manufacturer = company,
            type = form.orEmpty(),
            category = consumerCategory.orEmpty(),
            scientificName = scientificName,
            scientificCategory = scientificCategory.orEmpty(),
            route = route,
            isFavorite = false
        )
    }
}
