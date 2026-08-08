package com.medsy.data.prescription.mapper

import com.medsy.data.prescription.remote.dto.AnalyzedMedicineImageDto
import com.medsy.data.prescription.remote.dto.ExtractedMedicineDto
import com.medsy.data.prescription.remote.dto.MedicineCandidateDto
import com.medsy.data.prescription.remote.dto.PrescriptionAnalysisDto
import com.medsy.data.productdetails.remote.ProductDetailsDto
import com.medsy.data.search.dto.ProductDto
import com.medsy.domain.prescription.model.ExtractedMedicine
import com.medsy.domain.prescription.model.MatchStatus
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome

fun PrescriptionAnalysisDto.toDomain(): PrescriptionExtractionOutcome {
    if (medicines.isEmpty()) return PrescriptionExtractionOutcome.NoMedicines
    return PrescriptionExtractionOutcome.MedicinesDetected(
        medicines = medicines.map { it.toDomain() }
    )
}

fun ExtractedMedicineDto.toDomain(): ExtractedMedicine {
    val candidateMedicines = candidates.map { it.toDomain() }
    return ExtractedMedicine(
        localItemId = localItemId,
        rawText = rawText,
        extractedName = extractedName,
        extractedStrength = extractedStrength,
        extractedForm = extractedForm,
        matchStatus = if (matchStatus == "MATCHED") MatchStatus.MATCHED else MatchStatus.NOT_FOUND,
        confidence = confidence,
        candidates = candidateMedicines,
        selectedMedicine = candidateMedicines.firstOrNull(),
        isConfirmed = matchStatus == "MATCHED"
    )
}

fun MedicineCandidateDto.toDomain(): Medicine {
    return Medicine(
        productId = productId,
        name = name,
        strength = strength,
        form = form,
        price = price,
        imageUrl = imageUrl
    )
}

fun AnalyzedMedicineImageDto.toDomain(): Medicine {
    return Medicine(
        productId = id ?: 0,
        name = name ?: productName ?: "",
        strength = scientificName,
        form = route,
        price = price?.toInt() ?: 0,
        imageUrl = imageUrl
    )
}

fun ProductDetailsDto.toMedicine(): Medicine {
    return Medicine(
        productId = id,
        name = name,
        strength = scientificName,
        form = route,
        price = price.toInt(),
        imageUrl = imageUrl
    )
}

fun ProductDto.toMedicine(): Medicine {
    return Medicine(
        productId = id,
        name = name,
        strength = strength ?: scientificName,
        form = form ?: route,
        price = price.toInt(),
        imageUrl = imageUrl,
    )
}
