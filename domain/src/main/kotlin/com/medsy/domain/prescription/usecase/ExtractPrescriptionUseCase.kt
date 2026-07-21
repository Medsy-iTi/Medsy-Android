package com.medsy.domain.prescription.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.model.PrescriptionMedicine
import com.medsy.domain.prescription.model.RecognitionStatus
import com.medsy.domain.prescription.repository.PrescriptionRepository
import com.medsy.domain.search.model.SearchProduct
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject

class ExtractPrescriptionUseCase @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(
        image: PrescriptionImage,
    ): MedsyResult<PrescriptionExtractionOutcome, MedsyError> =
        when (val mockResult = prescriptionRepository.extractPrescription(image)) {
            is MedsyResult.Error -> mockResult
            is MedsyResult.Success -> when (mockResult.data) {
                is PrescriptionExtractionOutcome.MedicinesDetected -> loadProducts()
                PrescriptionExtractionOutcome.NoMedicines -> mockResult
                PrescriptionExtractionOutcome.Unreadable -> mockResult
            }
        }

    private suspend fun loadProducts(): MedsyResult<PrescriptionExtractionOutcome, MedsyError.Remote> =
        when (
            val result = searchRepository.getProducts(
                page = 0,
                size = PRODUCT_COUNT,
                sort = listOf("id,asc"),
            )
        ) {
            is MedsyResult.Error -> result
            is MedsyResult.Success -> {
                val products = result.data.content
                if (products.isEmpty()) {
                    MedsyResult.Success(PrescriptionExtractionOutcome.NoMedicines)
                } else {
                    MedsyResult.Success(
                        PrescriptionExtractionOutcome.MedicinesDetected(
                            medicines = products.mapIndexed { index, product ->
                                PrescriptionMedicine(
                                    medicine = product.toMedicine(),
                                    recognitionStatus = if (index == products.lastIndex) {
                                        RecognitionStatus.NEEDS_REVIEW
                                    } else {
                                        RecognitionStatus.RECOGNIZED
                                    },
                                )
                            }
                        )
                    )
                }
            }
        }

    private fun SearchProduct.toMedicine(): Medicine = Medicine(
        id = id,
        name = name,
        packDescription = scientificName.ifBlank { company },
        unitPriceEgp = price.toInt(),
        imageUrl = imageUrl,
    )

    private companion object {
        const val PRODUCT_COUNT = 4
    }
}
