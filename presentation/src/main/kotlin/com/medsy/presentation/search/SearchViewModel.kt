package com.medsy.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.common.LocaleConstants
import com.medsy.domain.common.fold
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.search.model.SearchProduct
import com.medsy.domain.search.usecase.SearchProductsUseCase
import com.medsy.domain.favorites.usecase.GetFavoritesUseCase
import com.medsy.domain.favorites.usecase.AddFavoriteUseCase
import com.medsy.domain.favorites.usecase.RemoveFavoriteUseCase
import com.medsy.domain.favorites.model.FavoriteProduct
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
) : ViewModel() {

    private val allFetchedProducts = mutableListOf<SearchProduct>()

    private var searchJob: Job? = null
    private var fetchJob: Job? = null
    private val SEARCH_DEBOUNCE_MILLIS = 300L


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
        loadCategories()
        observeSessionAndFavorites()
    }

    fun onIntent(intent: SearchUIIntent) {
        when (intent) {
            is SearchUIIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.value) }
                search(intent.value)
            }

            SearchUIIntent.ClearQueryClicked -> {
                _state.update { it.copy(query = "") }
                search("")
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
                    SearchFilterId.CATEGORY.name -> {
                        _state.value = _state.value.copy(isCategoryBottomSheetOpen = true)
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

            is SearchUIIntent.CategoryOptionSelected -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedCategory = intent.category,
                        isCategoryBottomSheetOpen = false
                    )
                }
                reloadProducts()
            }

            SearchUIIntent.DismissBottomSheet -> {
                _state.value = _state.value.copy(
                    isSortBottomSheetOpen = false,
                    isPriceBottomSheetOpen = false,
                    isCategoryBottomSheetOpen = false
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
                toggleFavorite(intent.productId)
            }

            is SearchUIIntent.AddToCartClicked -> addToCart(intent.productId)
        }
    }

    private fun addToCart(rawProductId: String) {
        val productId = rawProductId.toIntOrNull()
        if (productId == null) {
            sendEffect(SearchUIEffect.ShowMessage(R.string.error_invalid_id))
            return
        }
        viewModelScope.launch {
            addCartItem(productId)
                .onSuccess {
                    sendEffect(SearchUIEffect.ShowMessage(R.string.search_added_to_cart))
                }
                .onError { error ->
                    sendEffect(SearchUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase(page = 0, size = 50).collect { result ->
                result.onSuccess { categories ->
                    _state.update { it.copy(categories = categories) }
                }
            }
        }
    }

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotBlank()) {
                delay(SEARCH_DEBOUNCE_MILLIS)
            }
            reloadProductsInternal()
        }
    }

    private fun reloadProducts() {
        searchJob?.cancel()
        reloadProductsInternal()
    }

    private fun reloadProductsInternal() {
        _state.update { currentState ->
            currentState.copy(
                isLoading = true,
                currentPage = 0,
                errorMessage = null
            )
        }
        allFetchedProducts.clear()
        fetchPage(0)
    }

    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.isLoadMore || currentState.isLastPage) return

        _state.update { it.copy(isLoadMore = true) }
        fetchPage(currentState.currentPage + 1)
    }

    private fun fetchPage(page: Int) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val result = searchProductsUseCase(
                query = _state.value.query,
                page = page,
                size = 20,
                sort = listOf(_state.value.selectedSort.apiValue),
                categoryId = _state.value.selectedCategory?.id
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
        _state.update { currentState ->
            currentState.copy(
                products = allFetchedProducts.map { it.toUi() }
            )
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
            _state.update { it.copy(favoriteProductIds = emptySet()) }
            return
        }
        favoritesJob = viewModelScope.launch {
            getFavoritesUseCase(userId).collect { favorites ->
                _state.update { currentState ->
                    currentState.copy(
                        favoriteProductIds = favorites.map { it.id.toString() }.toSet()
                    )
                }
            }
        }
    }

    private fun toggleFavorite(productId: String) {
        val idInt = productId.toIntOrNull() ?: return
        val userId = currentUserId
        if (userId == 0L) return

        val currentFavorites = _state.value.favoriteProductIds
        val isFav = productId in currentFavorites

        viewModelScope.launch {
            if (isFav) {
                val product = allFetchedProducts.find { it.id == idInt }
                val productName = product?.name ?: ""
                removeFavoriteUseCase(idInt, userId)
                    .onSuccess {
                        sendEffect(
                            SearchUIEffect.ShowMessage(
                                R.string.search_removed_from_favorites,
                                listOf(productName)
                            )
                        )
                    }
            } else {
                val product = allFetchedProducts.find { it.id == idInt }
                if (product != null) {
                    addFavoriteUseCase(product.toFavorite(), userId)
                        .onSuccess {
                            sendEffect(
                                SearchUIEffect.ShowMessage(
                                    R.string.search_added_to_favorites,
                                    listOf(product.name)
                                )
                            )
                        }
                }
            }
        }
    }

    private fun SearchProduct.toFavorite(): FavoriteProduct {
        return FavoriteProduct(
            id = id,
            name = name,
            arabicName = arabicName,
            scientificName = scientificName,
            price = price,
            imageUrl = imageUrl,
            categoryId = categoryId,
            categoryName = categoryName,
            company = company,
            route = route
        )
    }

    private fun sendEffect(effect: SearchUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
