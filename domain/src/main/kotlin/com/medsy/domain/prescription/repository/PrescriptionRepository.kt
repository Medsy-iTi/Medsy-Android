package com.medsy.domain.prescription.repository

import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage

interface PrescriptionRepository {
    suspend fun prepareCameraImage(): Result<PrescriptionImage>
    suspend fun importGalleryImage(sourceUri: String): Result<PrescriptionImage>
    suspend fun deleteImage(image: PrescriptionImage): Result<Unit>
    suspend fun extractPrescription(image: PrescriptionImage): Result<PrescriptionExtractionOutcome>
    suspend fun searchMedicines(query: String): Result<List<Medicine>>
    suspend fun addPrescriptionToCart(request: PrescriptionCartRequest): Result<Unit>
}
