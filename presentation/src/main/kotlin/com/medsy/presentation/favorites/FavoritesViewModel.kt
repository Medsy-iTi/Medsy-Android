package com.medsy.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.LocaleConstants
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.favorites.model.FavoriteProduct
import com.medsy.domain.favorites.usecase.GetFavoritesUseCase
import com.medsy.domain.favorites.usecase.RemoveFavoriteUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.search.SearchProductUi
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
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
) : ViewModel() {

    private val allFavorites = mutableListOf<FavoriteProduct>()

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
        observeSessionAndFavorites()
    }

    fun onIntent(intent: FavoritesUIIntent) {
        when (intent) {
            is FavoritesUIIntent.BackClicked -> sendEffect(FavoritesUIEffect.NavigateBack)

            is FavoritesUIIntent.ProductClicked ->
                sendEffect(FavoritesUIEffect.NavigateToProductDetails(intent.productId))

            is FavoritesUIIntent.FavoriteClicked -> {
                val idInt = intent.productId.toIntOrNull() ?: return
                val userId = currentUserId
                if (userId == 0L) return
                viewModelScope.launch {
                    val product = allFavorites.find { it.id == idInt }
                    val productName = product?.name ?: ""
                    removeFavoriteUseCase(idInt, userId)
                        .onSuccess {
                            sendEffect(
                                FavoritesUIEffect.ShowMessage(
                                    R.string.search_removed_from_favorites,
                                    listOf(productName),
                                    isSuccess = true,
                                )
                            )
                        }
                        .onError { error ->
                            sendEffect(
                                FavoritesUIEffect.ShowMessage(
                                    messageRes = error.toMessageRes()
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
                    sendEffect(
                        FavoritesUIEffect.ShowMessage(
                            R.string.search_added_to_cart,
                            isSuccess = true,
                        )
                    )
                }
                .onError { error ->
                    sendEffect(FavoritesUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private var currentUserId: Long = 0L
    private var favoritesJob: Job? = null

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
            _state.update { it.copy(products = emptyList(), isLoading = false) }
            return
        }
        _state.update { it.copy(isLoading = true) }
        favoritesJob = viewModelScope.launch {
            getFavoritesUseCase(userId).collect { result ->
                result.onSuccess { favorites ->
                    allFavorites.clear()
                    allFavorites.addAll(favorites)
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            products = favorites.map { it.toUi() }
                        )
                    }
                }.onError { error ->
                    _state.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
                    sendEffect(
                        FavoritesUIEffect.ShowMessage(
                            messageRes = error.toMessageRes()
                        )
                    )
                }
            }
        }
    }

    private fun FavoriteProduct.toUi(): SearchProductUi {
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
