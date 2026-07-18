package com.medsy.presentation.categories

import com.medsy.presentation.home.CategoryUi

data class CategoriesUIState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val searchQuery: String = "",
    val categories: List<CategoryUi> = emptyList(),
    val filteredCategories: List<CategoryUi> = emptyList()
)
