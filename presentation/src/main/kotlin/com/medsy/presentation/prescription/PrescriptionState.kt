package com.medsy.presentation.prescription

import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.model.PrescriptionMedicine
import com.medsy.domain.prescription.model.RecognitionStatus

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
    val targetMedicineId: Int? = null,
    val returnStep: PrescriptionStep = PrescriptionStep.MEDICINE_REVIEW,
    val query: String = "",
    val results: List<Medicine> = emptyList(),
    val isLoading: Boolean = false,
)

data class PrescriptionState(
    val isAttachmentOnly: Boolean = false,
    val step: PrescriptionStep = PrescriptionStep.SOURCE_SELECTION,
    val image: PrescriptionImage? = null,
    val medicines: List<PrescriptionMedicine> = emptyList(),
    val picker: MedicinePickerState = MedicinePickerState(),
    val isPreparingImage: Boolean = false,
    val isPrescriptionExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
    val isPartialSubmission: Boolean = false,
) {
    val recognizedCount: Int
        get() = medicines.count { it.recognitionStatus == RecognitionStatus.RECOGNIZED }

    val needsReviewCount: Int
        get() = medicines.count { it.recognitionStatus == RecognitionStatus.NEEDS_REVIEW }

    val canSubmit: Boolean
        get() = medicines.isNotEmpty() && needsReviewCount == 0 && !isSubmitting

    val totalPriceEgp: Int
        get() = medicines.sumOf { it.medicine.unitPriceEgp * it.quantity }
}
