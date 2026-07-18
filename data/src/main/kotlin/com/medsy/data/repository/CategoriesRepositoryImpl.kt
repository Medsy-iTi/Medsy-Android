package com.medsy.data.repository

import com.medsy.data.remote.datasource.categories.CategoriesRemoteDataSource
import com.medsy.data.remote.mapper.toDomain
import com.medsy.domain.categories.model.Category
import com.medsy.domain.categories.model.CategorySortField
import com.medsy.domain.categories.model.SortOrder
import com.medsy.domain.categories.repository.CategoriesRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesRemoteDataSource: CategoriesRemoteDataSource
) : CategoriesRepository {
    override fun getCategories(
        page: Int,
        size: Int,
        sortField: CategorySortField,
        sortOrder: SortOrder
    ): Flow<MedsyResult<List<Category>, MedsyError.Remote>> =
        flow {
            val sortParam = "${sortField.value},${sortOrder.value}"
            emit(
                categoriesRemoteDataSource.getCategories(page, size, sortParam)
                    .map { response -> response.content.map { it.toDomain() } }
            )
        }
}
