package com.medsy.domain.prescription.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage

interface PrescriptionRepository {
    suspend fun prepareCameraImage(): MedsyResult<PrescriptionImage, MedsyError.Local>
    suspend fun importGalleryImage(
        sourceUri: String,
    ): MedsyResult<PrescriptionImage, MedsyError.Local>

    suspend fun deleteImage(image: PrescriptionImage): EmptyMedsyResult<MedsyError.Local>
    suspend fun extractPrescription(
        image: PrescriptionImage,
    ): MedsyResult<PrescriptionExtractionOutcome, MedsyError>

    suspend fun analyzeMedicineImage(
        image: PrescriptionImage,
    ): MedsyResult<List<Medicine>, MedsyError>

    suspend fun searchMedicines(
        query: String,
    ): MedsyResult<List<Medicine>, MedsyError.Remote>

    suspend fun getMedicineById(
        productId: Int,
    ): MedsyResult<Medicine, MedsyError.Remote>

    suspend fun addPrescriptionToCart(
        request: PrescriptionCartRequest,
    ): EmptyMedsyResult<MedsyError.Remote>
}
