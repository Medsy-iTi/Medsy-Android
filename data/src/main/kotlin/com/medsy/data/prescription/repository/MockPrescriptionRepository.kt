package com.medsy.data.prescription.repository

import com.medsy.data.prescription.local.PrescriptionImageStorage
import com.medsy.data.prescription.mock.MockPrescriptionScenario
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.model.PrescriptionMedicine
import com.medsy.domain.prescription.model.RecognitionStatus
import com.medsy.domain.prescription.repository.PrescriptionRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@Singleton
class MockPrescriptionRepository @Inject constructor(
    private val imageStorage: PrescriptionImageStorage,
    private val scenario: MockPrescriptionScenario,
) : PrescriptionRepository {

    override suspend fun prepareCameraImage(): Result<PrescriptionImage> =
        imageOperation { imageStorage.prepareCameraImage() }

    override suspend fun importGalleryImage(sourceUri: String): Result<PrescriptionImage> =
        imageOperation { imageStorage.importGalleryImage(sourceUri) }

    override suspend fun deleteImage(image: PrescriptionImage): Result<Unit> =
        imageOperation {
            imageStorage.delete(image)
            Unit
        }

    override suspend fun extractPrescription(
        image: PrescriptionImage,
    ): Result<PrescriptionExtractionOutcome> {
        delay(EXTRACTION_DELAY_MILLIS)
        return when (scenario) {
            MockPrescriptionScenario.SUCCESS -> Result.success(
                PrescriptionExtractionOutcome.MedicinesDetected(mockExtractedMedicines()),
            )

            MockPrescriptionScenario.UPLOAD_FAILURE -> Result.failure(IOException())
            MockPrescriptionScenario.UNREADABLE -> Result.success(
                PrescriptionExtractionOutcome.Unreadable,
            )

            MockPrescriptionScenario.NO_MEDICINES -> Result.success(
                PrescriptionExtractionOutcome.NoMedicines,
            )
        }
    }

    override suspend fun searchMedicines(query: String): Result<List<Medicine>> {
        val normalized = query.trim()
        val medicines = if (normalized.isEmpty()) {
            medicineCatalog
        } else {
            medicineCatalog.filter {
                it.name.contains(normalized, ignoreCase = true) ||
                it.packDescription.contains(normalized, ignoreCase = true)
            }
        }
        return Result.success(medicines)
    }

    override suspend fun addPrescriptionToCart(
        request: PrescriptionCartRequest,
    ): Result<Unit> = Result.success(Unit)

    private fun mockExtractedMedicines() = listOf(
        PrescriptionMedicine(medicineCatalog[0], recognitionStatus = RecognitionStatus.RECOGNIZED),
        PrescriptionMedicine(medicineCatalog[1], recognitionStatus = RecognitionStatus.RECOGNIZED),
        PrescriptionMedicine(medicineCatalog[2], recognitionStatus = RecognitionStatus.NEEDS_REVIEW),
        PrescriptionMedicine(medicineCatalog[3], recognitionStatus = RecognitionStatus.RECOGNIZED),
    )

    private inline fun <T> imageOperation(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    private companion object {
        const val EXTRACTION_DELAY_MILLIS = 5_000L

        val medicineCatalog = listOf(
            Medicine("panadol-extra", "بانادول إكسترا", "20 قرص", 45),
            Medicine("amoxicillin-500", "أموكسيسيلين 500", "12 كبسولة", 38),
            Medicine("augmentin-1g", "أوجمنتين 1 جم", "شريط مضاد حيوي", 210),
            Medicine("omeprazole-20", "أوميبرازول 20", "14 كبسولة", 22),
            Medicine("panadol-regular", "بانادول عادي", "20 قرص", 35),
            Medicine("panadol-sinus", "بانادول ساينس", "20 قرص", 52),
            Medicine("cetirizine-10", "سيتريزين 10", "20 قرص", 30),
            Medicine("vitamin-c-1000", "فيتامين سي 1000", "10 أقراص", 65),
        )
    }
}
