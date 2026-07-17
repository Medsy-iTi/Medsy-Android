package com.medsy.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.home.CategoryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesUIState())
    val state: StateFlow<CategoriesUIState> = _state.asStateFlow()

    private val _effect = Channel<CategoriesUIEffect>()
    val effect = _effect.receiveAsFlow()

    private var allCategories = listOf<CategoryUi>()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCategoriesUseCase(page = 0, size = 100).collectLatest { result ->
                result.onSuccess { domainCategories ->
                    allCategories = domainCategories.map {
                        CategoryUi(
                            id = it.id.toString(),
                            name = it.name
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            categories = allCategories,
                            filteredCategories = filterCategories(it.searchQuery)
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
            }
        }
    }

    private fun filterCategories(query: String): List<CategoryUi> {
        return if (query.isBlank()) {
            allCategories
        } else {
            allCategories.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun onIntent(intent: CategoriesUIIntent) {
        when (intent) {
            is CategoriesUIIntent.OnBackClick -> sendEffect(CategoriesUIEffect.NavigateBack)

            is CategoriesUIIntent.OnSearchQueryChange -> {
                val query = intent.query
                _state.update {
                    it.copy(
                        searchQuery = query,
                        filteredCategories = filterCategories(query)
                    )
                }
            }

            is CategoriesUIIntent.OnCategoryClick ->
                sendEffect(CategoriesUIEffect.NavigateToCategory(intent.categoryId))
        }
    }

    private fun sendEffect(effect: CategoriesUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}