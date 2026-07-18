package com.medsy.domain.categories.repository

import com.medsy.domain.categories.model.Category
import com.medsy.domain.categories.model.CategorySortField
import com.medsy.domain.categories.model.SortOrder
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(
        page: Int,
        size: Int,
        sortField: CategorySortField,
        sortOrder: SortOrder
    ): Flow<MedsyResult<List<Category>, MedsyError.Remote>>
}
