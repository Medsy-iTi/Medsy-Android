package com.medsy.domain.categories.usecase

import com.medsy.domain.categories.model.Category
import com.medsy.domain.categories.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke(
        page: Int,
        size: Int,
        sort: String = "name,ASC"
    ): Flow<Result<List<Category>>> {
        return categoriesRepository.getCategories(page, size, sort)
    }
}
