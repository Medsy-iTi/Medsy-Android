package com.medsy.domain.categories.usecase

import com.medsy.domain.categories.model.Category
import com.medsy.domain.categories.model.CategorySortField
import com.medsy.domain.categories.model.SortOrder
import com.medsy.domain.categories.repository.CategoriesRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke(
        page: Int,
        size: Int,
        sortField: CategorySortField = CategorySortField.NAME,
        sortOrder: SortOrder = SortOrder.ASC
    ): Flow<MedsyResult<List<Category>, MedsyError.Remote>> {
        return categoriesRepository.getCategories(page, size, sortField, sortOrder)
    }
}
