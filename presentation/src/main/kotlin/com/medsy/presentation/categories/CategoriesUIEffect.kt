package com.medsy.presentation.categories

sealed interface CategoriesUIEffect {
    object NavigateBack : CategoriesUIEffect
    data class NavigateToCategory(val categoryId: Int, val categoryName: String) : CategoriesUIEffect
}
