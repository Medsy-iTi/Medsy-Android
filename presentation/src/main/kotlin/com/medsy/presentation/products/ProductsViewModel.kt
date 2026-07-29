package com.medsy.presentation.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.products.usecase.GetProductsByCategoryUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsUIState())
    val state: StateFlow<ProductsUIState> = _state.asStateFlow()

    private val _effect = Channel<ProductsUIEffect>()
    val effect = _effect.receiveAsFlow()

    init {
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
        }
    }

    private fun addToCart(productId: Int) {
        viewModelScope.launch {
            addCartItem(productId)
                .onSuccess {
                    sendEffect(
                        ProductsUIEffect.ShowMessage(R.string.products_added_to_cart)
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
                    val uiProducts = domainProducts.map {
                        ProductUi(
                            id = it.id,
                            name = it.name,
                            scientificName = it.scientificName,
                            price = it.price.toString(),
                            imageUrl = it.imageUrl
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

    private fun sendEffect(effect: ProductsUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
