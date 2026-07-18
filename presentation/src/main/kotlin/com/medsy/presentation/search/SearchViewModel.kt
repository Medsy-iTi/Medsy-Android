package com.medsy.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.fold
import com.medsy.domain.search.model.SearchProduct
import com.medsy.domain.search.usecase.SearchProductsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val allFetchedProducts = mutableListOf<SearchProduct>()


    private val _state = MutableStateFlow(SearchState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchState()
        )

    private val _effect = Channel<SearchUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        reloadProducts()
    }

    fun onIntent(intent: SearchUIIntent) {
        when (intent) {
            is SearchUIIntent.QueryChanged -> {
                _state.value = _state.value.copy(query = intent.value)
                updateProductsUiList()
            }

            SearchUIIntent.ClearQueryClicked -> {
                _state.value = _state.value.copy(query = "")
                updateProductsUiList()
            }

            SearchUIIntent.BackClicked -> sendEffect(SearchUIEffect.NavigateBack)

            is SearchUIIntent.FilterChipClicked -> {
                when (intent.filterId) {
                    SearchFilterId.SORT.name -> {
                        _state.value = _state.value.copy(isSortBottomSheetOpen = true)
                    }
                    SearchFilterId.PRICE.name -> {
                        _state.value = _state.value.copy(isPriceBottomSheetOpen = true)
                    }
                }
            }
            is SearchUIIntent.SortOptionSelected -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedSort = intent.option,
                        isSortBottomSheetOpen = false
                    )
                }
                reloadProducts()
            }


            is SearchUIIntent.PriceFilterOptionSelected -> {
                _state.value = _state.value.copy(
                    isPriceBottomSheetOpen = false
                )
                updateProductsUiList()
            }

            SearchUIIntent.DismissBottomSheet -> {
                _state.value = _state.value.copy(
                    isSortBottomSheetOpen = false,
                    isPriceBottomSheetOpen = false
                )
            }

            SearchUIIntent.LoadNextPage -> {
                loadNextPage()
            }

            SearchUIIntent.RetryClicked -> {
                reloadProducts()
            }

            is SearchUIIntent.ProductClicked ->
                sendEffect(SearchUIEffect.NavigateToProductDetails(intent.productId))

            is SearchUIIntent.FavoriteClicked -> {
                _state.value = _state.value.copy(
                    favoriteProductIds = _state.value.favoriteProductIds.let { current ->
                        if (intent.productId in current) current - intent.productId else current + intent.productId
                    }
                )
            }

            is SearchUIIntent.AddToCartClicked -> {
                sendEffect(SearchUIEffect.ShowMessage(R.string.search_added_to_cart))
            }
        }
    }

    private fun reloadProducts() {
        _state.value = _state.value.copy(
            isLoading = true,
            currentPage = 0,
            errorMessage = null
        )
        allFetchedProducts.clear()
        fetchPage(0)
    }

    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.isLoadMore || currentState.isLastPage) return

        _state.value = _state.value.copy(isLoadMore = true)
        fetchPage(currentState.currentPage + 1)
    }

    private fun fetchPage(page: Int) {
        viewModelScope.launch {
            val result = searchProductsUseCase(
                page = page,
                size = 20,
                sort = listOf(_state.value.selectedSort.apiValue)
            )

            result.fold(
                onSuccess = { pageData ->
                    android.util.Log.d("API_DEBUG", "Received ${pageData.content.size} products")
                    if (page == 0) allFetchedProducts.clear()
                    allFetchedProducts.addAll(pageData.content)

                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isLoadMore = false,
                            currentPage = pageData.pageNumber,
                            totalPages = pageData.totalPages,
                            isLastPage = pageData.isLast,
                            errorMessage = null
                        )
                    }
                    updateProductsUiList()
                },
                onError = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadMore = false,
                            errorMessage = error.toMessageRes(),
                        )
                    }
                }
            )
        }
    }

    private fun updateProductsUiList() {
        val query = _state.value.query
        val filtered = allFetchedProducts.filter { product ->
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.arabicName.contains(query, ignoreCase = true) ||
                    product.scientificName.contains(query, ignoreCase = true)


            matchesQuery
        }

        _state.value = _state.value.copy(
            products = filtered.map { it.toUi() }
        )
    }

    private fun SearchProduct.toUi(): SearchProductUi {
        val isArabic = java.util.Locale.getDefault().language == "ar"
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

    private fun sendEffect(effect: SearchUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
