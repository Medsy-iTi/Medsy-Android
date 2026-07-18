package com.medsy.presentation.prescription

sealed interface PrescriptionUIIntent {
    data object BackClicked : PrescriptionUIIntent
    data object CameraClicked : PrescriptionUIIntent
    data object GalleryClicked : PrescriptionUIIntent
    data class CameraCaptureCompleted(val success: Boolean) : PrescriptionUIIntent
    data class GalleryImageSelected(val uri: String?) : PrescriptionUIIntent
    data object ChangeImageClicked : PrescriptionUIIntent
    data object DeleteImageClicked : PrescriptionUIIntent
    data object ReviewImageClicked : PrescriptionUIIntent
    data object RetryExtractionClicked : PrescriptionUIIntent
    data object ChooseAnotherImageClicked : PrescriptionUIIntent
    data object TogglePrescriptionImageClicked : PrescriptionUIIntent
    data class ConfirmMedicineClicked(val medicineId: String) : PrescriptionUIIntent
    data class EditMedicineClicked(val medicineId: String) : PrescriptionUIIntent
    data class DeleteMedicineClicked(val medicineId: String) : PrescriptionUIIntent
    data class IncreaseQuantityClicked(val medicineId: String) : PrescriptionUIIntent
    data class DecreaseQuantityClicked(val medicineId: String) : PrescriptionUIIntent
    data object AddMedicineManuallyClicked : PrescriptionUIIntent
    data class MedicineQueryChanged(val query: String) : PrescriptionUIIntent
    data class MedicineSelected(val medicineId: String) : PrescriptionUIIntent
    data object AddToCartClicked : PrescriptionUIIntent
    data object ViewCartClicked : PrescriptionUIIntent
    data object ReturnHomeClicked : PrescriptionUIIntent
}
