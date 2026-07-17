package com.medsy.data.repository

import com.medsy.data.remote.datasource.categories.CategoriesRemoteDataSource
import com.medsy.data.remote.mapper.toDomain
import com.medsy.data.remote.network.ApiResult
import com.medsy.domain.categories.model.Category
import com.medsy.domain.categories.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoriesRemoteDataSource: CategoriesRemoteDataSource
) : CategoriesRepository {
    override fun getCategories(page: Int, size: Int, sort: String): Flow<Result<List<Category>>> =
        flow {
            when (val response = categoriesRemoteDataSource.getCategories(page, size, sort)) {
                is ApiResult.Success -> {
                    val domainCategories =
                        response.data?.content?.map { it.toDomain() } ?: emptyList()
                    emit(Result.success(domainCategories))
                }

                is ApiResult.Error -> {
                    emit(Result.failure(Exception(response.error.toString())))
                }
            }
        }
}