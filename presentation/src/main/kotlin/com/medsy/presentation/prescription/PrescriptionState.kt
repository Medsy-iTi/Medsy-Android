package com.medsy.presentation.prescription

import com.medsy.domain.prescription.model.ExtractedMedicine
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionImage

enum class PrescriptionStep {
    SOURCE_SELECTION,
    IMAGE_PREVIEW,
    EXTRACTING,
    MEDICINE_REVIEW,
    MEDICINE_PICKER,
    UPLOAD_ERROR,
    UNREADABLE,
    NO_MEDICINES,
    CONFIRMATION,
}

enum class MedicinePickerMode {
    ADD,
    REPLACE,
}

data class MedicinePickerState(
    val mode: MedicinePickerMode = MedicinePickerMode.ADD,
    val targetMedicineId: String? = null,
    val returnStep: PrescriptionStep = PrescriptionStep.MEDICINE_REVIEW,
    val query: String = "",
    val results: List<Medicine> = emptyList(),
    val isLoading: Boolean = false,
)

data class PrescriptionState(
    val step: PrescriptionStep = PrescriptionStep.SOURCE_SELECTION,
    val image: PrescriptionImage? = null,
    val medicines: List<ExtractedMedicine> = emptyList(),
    val picker: MedicinePickerState = MedicinePickerState(),
    val isPreparingImage: Boolean = false,
    val isPrescriptionExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    val confirmedCount: Int
        get() = medicines.count { it.isConfirmed }

    val needsReviewCount: Int
        get() = medicines.count { !it.isConfirmed }

    val canSubmit: Boolean
        get() = medicines.isNotEmpty() && needsReviewCount == 0 && !isSubmitting

    val totalEgp: Int
        get() = medicines.sumOf { (it.selectedMedicine?.price ?: 0) * it.quantity }
}
