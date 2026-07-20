package com.medsy.domain.prescription.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.search.model.SearchProduct
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject

class SearchPrescriptionMedicinesUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(
        query: String,
    ): MedsyResult<List<Medicine>, MedsyError.Remote> =
        when (val result = loadProducts(query)) {
            is MedsyResult.Error -> result
            is MedsyResult.Success -> MedsyResult.Success(
                result.data.content.map { it.toMedicine() }
            )
        }

    private suspend fun loadProducts(query: String) =
        if (query.isBlank()) {
            repository.getProducts(
                page = 0,
                size = PAGE_SIZE,
                sort = listOf("name,asc"),
            )
        } else {
            repository.searchProducts(
                keyword = query,
                page = 0,
                size = PAGE_SIZE,
                sort = listOf("name,asc"),
            )
        }

    private fun SearchProduct.toMedicine(): Medicine = Medicine(
        id = id,
        name = name,
        packDescription = scientificName.ifBlank { company },
        unitPriceEgp = price.toInt(),
        imageUrl = imageUrl,
    )

    private companion object {
        const val PAGE_SIZE = 20
    }
}
