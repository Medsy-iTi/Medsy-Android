package com.medsy.domain.categories.repository

import com.medsy.domain.categories.model.Category
import com.medsy.domain.categories.model.CategorySortField
import com.medsy.domain.categories.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(
        page: Int,
        size: Int,
        sortField: CategorySortField,
        sortOrder: SortOrder
    ): Flow<Result<List<Category>>>
}
