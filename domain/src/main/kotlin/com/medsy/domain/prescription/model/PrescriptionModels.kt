package com.medsy.domain.prescription.model

data class PrescriptionImage(
    val uri: String,
    val storageKey: String,
)

data class Medicine(
    val id: String,
    val name: String,
    val packDescription: String,
    val unitPriceEgp: Int,
)

enum class RecognitionStatus {
    RECOGNIZED,
    NEEDS_REVIEW,
}

data class PrescriptionMedicine(
    val medicine: Medicine,
    val quantity: Int = 1,
    val recognitionStatus: RecognitionStatus,
)

sealed interface PrescriptionExtractionOutcome {
    data class MedicinesDetected(
        val medicines: List<PrescriptionMedicine>,
    ) : PrescriptionExtractionOutcome

    data object Unreadable : PrescriptionExtractionOutcome
    data object NoMedicines : PrescriptionExtractionOutcome
}

data class PrescriptionCartRequest(
    val prescriptionImage: PrescriptionImage,
    val medicines: List<PrescriptionMedicine>,
)
