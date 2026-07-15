package com.medsy.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private var hasLoadedInitialData = false

    private val allProducts = mockProducts()

    private val _state = MutableStateFlow(SearchState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                _state.value = _state.value.copy(products = allProducts)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchState()
        )

    private val _effect = Channel<SearchUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SearchUIIntent) {
        when (intent) {
            is SearchUIIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.value) }
                runSearch(intent.value)
            }

            SearchUIIntent.ClearQueryClicked -> {
                _state.update { it.copy(query = "") }
                runSearch("")
            }

            SearchUIIntent.BackClicked -> sendEffect(SearchUIEffect.NavigateBack)

            is SearchUIIntent.FilterChipClicked -> {
                _state.update {
                    it.copy(
                        filters = it.filters.map { chip ->
                            chip.copy(isSelected = chip.id == intent.filterId)
                        }
                    )
                }
            }

            is SearchUIIntent.ProductClicked ->
                sendEffect(SearchUIEffect.NavigateToProductDetails(intent.productId))

            is SearchUIIntent.FavoriteClicked -> {
                _state.update {
                    val current = it.favoriteProductIds
                    val updated = if (intent.productId in current) {
                        current - intent.productId
                    } else {
                        current + intent.productId
                    }
                    it.copy(favoriteProductIds = updated)
                }
            }

            is SearchUIIntent.AddToCartClicked -> {
                sendEffect(SearchUIEffect.ShowMessage(R.string.search_added_to_cart))
            }
        }
    }

    private fun runSearch(query: String) {
        val filtered = if (query.isBlank()) {
            allProducts
        } else {
            allProducts.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.update { it.copy(products = filtered) }
    }

    private fun sendEffect(effect: SearchUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private inline fun MutableStateFlow<SearchState>.update(block: (SearchState) -> SearchState) {
        value = block(value)
    }

    private fun mockProducts(): List<SearchProductUi> = listOf(
        SearchProductUi(
            id = "panadol-extra",
            name = "Panadol Extra",
            subtitle = "500mg - 24 tablets",
            priceEgp = 68,
        ),
        SearchProductUi(
            id = "panadol-sinus",
            name = "Panadol Sinus",
            subtitle = "20 tablets",
            priceEgp = 52,
        ),
        SearchProductUi(
            id = "panadol-kids",
            name = "Panadol for Kids",
            subtitle = "120mg/5ml - 60ml",
            priceEgp = 45,
        ),
        SearchProductUi(
            id = "panadol-cold-flu",
            name = "Panadol Cold & Flu",
            subtitle = "12+ years - 24 tablets",
            priceEgp = 60,
        ),
        SearchProductUi(
            id = "panadol-regular",
            name = "Panadol Regular",
            subtitle = "500mg - 20 tablets",
            priceEgp = 35,
        ),
    )
}
