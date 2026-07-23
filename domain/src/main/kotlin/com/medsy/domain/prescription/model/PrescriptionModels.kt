package com.medsy.domain.prescription.model

data class PrescriptionImage(
    val uri: String,
    val storageKey: String,
)

data class Medicine(
    val productId: Int,
    val name: String,
    val strength: String?,
    val form: String?,
    val price: Int,
    val imageUrl: String?,
)

enum class MatchStatus {
    MATCHED,
    NOT_FOUND,
}

data class ExtractedMedicine(
    val localItemId: String,
    val rawText: String,
    val extractedName: String?,
    val extractedStrength: String?,
    val extractedForm: String?,
    val matchStatus: MatchStatus,
    val confidence: Double,
    val candidates: List<Medicine>,
    val selectedMedicine: Medicine? = null,
    val quantity: Int = 1,
    val isConfirmed: Boolean = false,
)

sealed interface PrescriptionExtractionOutcome {
    data class MedicinesDetected(
        val medicines: List<ExtractedMedicine>,
    ) : PrescriptionExtractionOutcome

    data object Unreadable : PrescriptionExtractionOutcome
    data object NoMedicines : PrescriptionExtractionOutcome
}

data class PrescriptionCartRequest(
    val prescriptionImage: PrescriptionImage,
    val medicines: List<ExtractedMedicine>,
)
