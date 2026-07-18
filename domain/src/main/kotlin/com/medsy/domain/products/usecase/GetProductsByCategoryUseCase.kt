package com.medsy.domain.products.usecase

import com.medsy.domain.products.model.Product
import com.medsy.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    operator fun invoke(
        categoryId: Int,
        page: Int = 0,
        size: Int = 50,
        sort: String = "price,desc"
    ): Flow<Result<List<Product>>> {
        return productsRepository.getProductsByCategory(categoryId, page, size, sort)
    }
}
