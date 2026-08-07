package com.medsy.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.favorites.usecase.GetFavoritesUseCase
import com.medsy.domain.favorites.usecase.RemoveFavoriteUseCase
import com.medsy.domain.search.model.SearchProduct
import com.medsy.domain.common.LocaleConstants
import com.medsy.domain.common.onSuccess
import com.medsy.domain.common.onError
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.search.SearchProductUi
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
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addCartItem: AddCartItemUseCase,
) : ViewModel() {

    private val allFavorites = mutableListOf<SearchProduct>()

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesState()
        )

    private val _effect = Channel<FavoritesUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeFavorites()
    }

    fun onIntent(intent: FavoritesUIIntent) {
        when (intent) {
            is FavoritesUIIntent.BackClicked -> sendEffect(FavoritesUIEffect.NavigateBack)

            is FavoritesUIIntent.ProductClicked ->
                sendEffect(FavoritesUIEffect.NavigateToProductDetails(intent.productId))

            is FavoritesUIIntent.FavoriteClicked -> {
                val idInt = intent.productId.toIntOrNull() ?: return
                viewModelScope.launch {
                    val product = allFavorites.find { it.id == idInt }
                    val productName = product?.name ?: ""
                    removeFavoriteUseCase(idInt)
                        .onSuccess {
                            sendEffect(
                                FavoritesUIEffect.ShowMessage(
                                    R.string.search_removed_from_favorites,
                                    listOf(productName)
                                )
                            )
                        }
                }
            }

            is FavoritesUIIntent.AddToCartClicked -> {
                addToCart(intent.productId)
            }
        }
    }

    private fun addToCart(rawProductId: String) {
        val productId = rawProductId.toIntOrNull()
        if (productId == null) {
            sendEffect(FavoritesUIEffect.ShowMessage(R.string.error_invalid_id))
            return
        }
        viewModelScope.launch {
            addCartItem(productId)
                .onSuccess {
                    sendEffect(FavoritesUIEffect.ShowMessage(R.string.search_added_to_cart))
                }
                .onError { error ->
                    sendEffect(FavoritesUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun observeFavorites() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getFavoritesUseCase().collect { favorites ->
                allFavorites.clear()
                allFavorites.addAll(favorites)
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        products = favorites.map { it.toUi() }
                    )
                }
            }
        }
    }

    private fun SearchProduct.toUi(): SearchProductUi {
        val isArabic = Locale.getDefault().language == LocaleConstants.ARABIC_TAG
        val localizedName = if (isArabic && arabicName.isNotBlank()) arabicName else name
        val subtitle = if (scientificName.isNotBlank() && company.isNotBlank()) {
            "$scientificName · $company"
        } else if (scientificName.isNotBlank()) {
            scientificName
        } else {
            company
        }
        return SearchProductUi(
            id = id.toString(),
            name = localizedName,
            subtitle = subtitle,
            priceEgp = price.toInt(),
            imageUrl = imageUrl
        )
    }

    private fun sendEffect(effect: FavoritesUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
