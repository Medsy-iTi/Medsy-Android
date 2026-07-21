package com.medsy.domain.prescription.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
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
    ): MedsyResult<PrescriptionExtractionOutcome, MedsyError.Local>
}
