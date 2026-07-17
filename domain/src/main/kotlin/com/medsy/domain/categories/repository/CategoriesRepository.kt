package com.medsy.domain.categories.repository

import com.medsy.domain.categories.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(page: Int, size: Int, sort: String): Flow<Result<List<Category>>>
}
