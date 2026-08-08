package com.medsy.presentation.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.favorites.model.FavoriteProduct
import com.medsy.domain.favorites.usecase.AddFavoriteUseCase
import com.medsy.domain.favorites.usecase.GetFavoritesUseCase
import com.medsy.domain.favorites.usecase.RemoveFavoriteUseCase
import com.medsy.domain.products.model.Product
import com.medsy.domain.products.usecase.GetProductsByCategoryUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsUIState())
    val state: StateFlow<ProductsUIState> = _state.asStateFlow()

    private val _effect = Channel<ProductsUIEffect>()
    val effect = _effect.receiveAsFlow()

    private var fetchedDomainProducts: List<Product> = emptyList()
    private var currentUserId: Long = 0L
    private var favoritesJob: Job? = null

    init {
        observeSessionAndFavorites()
        val categoryId = savedStateHandle.get<String>("categoryId")?.toIntOrNull() ?: -1
        val categoryName = savedStateHandle.get<String>("categoryName") ?: ""
        if (categoryId != -1) {
            onIntent(ProductsUIIntent.LoadProducts(categoryId, categoryName))
        }
    }

    fun onIntent(intent: ProductsUIIntent) {
        when (intent) {
            is ProductsUIIntent.LoadProducts -> {
                _state.update {
                    it.copy(
                        categoryId = intent.categoryId,
                        categoryName = intent.categoryName
                    )
                }
                fetchProducts(intent.categoryId)
            }

            is ProductsUIIntent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = intent.query,
                        filteredProducts = filterProducts(it.products, intent.query)
                    )
                }
            }

            ProductsUIIntent.OnBackClick -> sendEffect(ProductsUIEffect.NavigateBack)
            is ProductsUIIntent.OnProductClick -> sendEffect(
                ProductsUIEffect.NavigateToProductDetails(
                    intent.productId
                )
            )

            is ProductsUIIntent.OnAddToCartClick -> addToCart(intent.productId)
            is ProductsUIIntent.OnFavoriteClick -> toggleFavorite(intent.productId)
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
            _state.update { currentState ->
                val updatedProducts = currentState.products.map { it.copy(isFavorite = false) }
                currentState.copy(
                    favoriteProductIds = emptySet(),
                    products = updatedProducts,
                    filteredProducts = filterProducts(updatedProducts, currentState.searchQuery)
                )
            }
            return
        }
        favoritesJob = viewModelScope.launch {
            getFavoritesUseCase(userId).collect { result ->
                result.onSuccess { favorites ->
                    val favIds = favorites.map { it.id }.toSet()
                    _state.update { currentState ->
                        val updatedProducts = currentState.products.map { product ->
                            product.copy(isFavorite = product.id in favIds)
                        }
                        currentState.copy(
                            favoriteProductIds = favIds,
                            products = updatedProducts,
                            filteredProducts = filterProducts(updatedProducts, currentState.searchQuery)
                        )
                    }
                }
            }
        }
    }

    private fun toggleFavorite(productId: Int) {
        val userId = currentUserId
        if (userId == 0L) return
        val domainProduct = fetchedDomainProducts.find { it.id == productId } ?: return
        val isFav = productId in _state.value.favoriteProductIds

        viewModelScope.launch {
            if (isFav) {
                removeFavoriteUseCase(productId, userId)
                    .onSuccess {
                        sendEffect(
                            ProductsUIEffect.ShowMessage(
                                R.string.search_removed_from_favorites,
                                listOf(domainProduct.name),
                                isSuccess = true,
                            )
                        )
                    }
            } else {
                addFavoriteUseCase(domainProduct.toFavorite(), userId)
                    .onSuccess {
                        sendEffect(
                            ProductsUIEffect.ShowMessage(
                                R.string.search_added_to_favorites,
                                listOf(domainProduct.name),
                                isSuccess = true,
                            )
                        )
                    }
            }
        }
    }

    private fun addToCart(productId: Int) {
        viewModelScope.launch {
            addCartItem(productId)
                .onSuccess {
                    sendEffect(
                        ProductsUIEffect.ShowMessage(R.string.products_added_to_cart, isSuccess = true)
                    )
                }
                .onError { error ->
                    sendEffect(ProductsUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun fetchProducts(categoryId: Int) {
        if (_state.value.products.isNotEmpty() && _state.value.categoryId == categoryId) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
            getProductsByCategoryUseCase(categoryId).collectLatest { result ->
                result.onSuccess { domainProducts ->
                    fetchedDomainProducts = domainProducts
                    val favIds = _state.value.favoriteProductIds
                    val uiProducts = domainProducts.map {
                        ProductUi(
                            id = it.id,
                            name = it.name,
                            scientificName = it.scientificName,
                            price = it.price.toString(),
                            imageUrl = it.imageUrl,
                            isFavorite = it.id in favIds
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = null,
                            products = uiProducts,
                            filteredProducts = filterProducts(uiProducts, it.searchQuery)
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
            }
        }
    }

    private fun filterProducts(products: List<ProductUi>, query: String): List<ProductUi> {
        return if (query.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    private fun Product.toFavorite(): FavoriteProduct {
        return FavoriteProduct(
            id = id,
            name = name,
            arabicName = name,
            scientificName = scientificName,
            price = price,
            imageUrl = imageUrl,
            categoryId = categoryId,
            categoryName = categoryName,
            company = company,
            route = route
        )
    }

    private fun sendEffect(effect: ProductsUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
