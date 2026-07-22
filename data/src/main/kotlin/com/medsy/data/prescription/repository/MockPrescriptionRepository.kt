package com.medsy.data.prescription.repository

import com.medsy.data.common.media.PrescriptionImageStorage
import com.medsy.data.prescription.mock.MockPrescriptionScenario
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.repository.PrescriptionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@Singleton
class MockPrescriptionRepository @Inject constructor(
    private val imageStorage: PrescriptionImageStorage,
    private val scenario: MockPrescriptionScenario,
) : PrescriptionRepository {
    override suspend fun prepareCameraImage(): MedsyResult<PrescriptionImage, MedsyError.Local> =
        imageOperation(imageStorage::prepareCameraImage)

    override suspend fun importGalleryImage(
        sourceUri: String,
    ): MedsyResult<PrescriptionImage, MedsyError.Local> =
        imageOperation { imageStorage.importGalleryImage(sourceUri) }

    override suspend fun deleteImage(
        image: PrescriptionImage,
    ): EmptyMedsyResult<MedsyError.Local> =
        imageOperation {
            imageStorage.delete(image)
            Unit
        }

    override suspend fun extractPrescription(
        image: PrescriptionImage,
    ): MedsyResult<PrescriptionExtractionOutcome, MedsyError.Local> {
        delay(EXTRACTION_DELAY_MILLIS)
        return when (scenario) {
            MockPrescriptionScenario.SUCCESS -> MedsyResult.Success(
                PrescriptionExtractionOutcome.MedicinesDetected(emptyList()),
            )

            MockPrescriptionScenario.UPLOAD_FAILURE ->
                MedsyResult.Error(MedsyError.Local.UNKNOWN)

            MockPrescriptionScenario.UNREADABLE -> MedsyResult.Success(
                PrescriptionExtractionOutcome.Unreadable,
            )

            MockPrescriptionScenario.NO_MEDICINES -> MedsyResult.Success(
                PrescriptionExtractionOutcome.NoMedicines,
            )
        }
    }

    private inline fun <T> imageOperation(
        block: () -> T,
    ): MedsyResult<T, MedsyError.Local> = try {
        MedsyResult.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        MedsyResult.Error(MedsyError.Local.MEDIA)
    }

    private companion object {
        const val EXTRACTION_DELAY_MILLIS = 5_000L
    }
}
