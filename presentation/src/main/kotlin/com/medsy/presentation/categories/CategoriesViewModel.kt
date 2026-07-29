package com.medsy.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.presentation.common.util.toMessageRes
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
        if (_state.value.categories.isNotEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
            getCategoriesUseCase(page = 0, size = 100).collectLatest { result ->
                result.onSuccess { domainCategories ->
                    allCategories = domainCategories.map {
                        CategoryUi(
                            id = it.id.toString(),
                            name = it.name,
                            imageRes = it.image
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = null,
                            categories = allCategories,
                            filteredCategories = filterCategories(it.searchQuery)
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

            is CategoriesUIIntent.OnCategoryClick -> {
                val category = allCategories.find { it.id == intent.categoryId }
                sendEffect(
                    CategoriesUIEffect.NavigateToCategory(
                        categoryId = intent.categoryId.toInt(),
                        categoryName = category?.name ?: ""
                    )
                )
            }
        }
    }

    private fun sendEffect(effect: CategoriesUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
