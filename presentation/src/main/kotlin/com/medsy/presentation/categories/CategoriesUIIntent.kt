package com.medsy.presentation.categories

sealed interface CategoriesUIIntent {
    object OnBackClick : CategoriesUIIntent
    data class OnSearchQueryChange(val query: String) : CategoriesUIIntent
    data class OnCategoryClick(val categoryId: String) : CategoriesUIIntent
}