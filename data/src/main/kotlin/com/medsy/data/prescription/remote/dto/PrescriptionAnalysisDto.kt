package com.medsy.data.prescription.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PrescriptionAnalysisDto(
    val medicines: List<ExtractedMedicineDto>
)

@JsonClass(generateAdapter = true)
data class ExtractedMedicineDto(
    val localItemId: String,
    val rawText: String,
    val extractedName: String?,
    val extractedStrength: String?,
    val extractedForm: String?,
    val matchStatus: String,
    val confidence: Double,
    val candidates: List<MedicineCandidateDto>
)

@JsonClass(generateAdapter = true)
data class MedicineCandidateDto(
    val productId: Int,
    val name: String,
    val strength: String?,
    val form: String?,
    val price: Int,
    val imageUrl: String?
)
