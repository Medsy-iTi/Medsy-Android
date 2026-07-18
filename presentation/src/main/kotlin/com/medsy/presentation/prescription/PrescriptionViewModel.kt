package com.medsy.presentation.prescription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.model.PrescriptionMedicine
import com.medsy.domain.prescription.model.RecognitionStatus
import com.medsy.domain.prescription.usecase.AddPrescriptionToCartUseCase
import com.medsy.domain.prescription.usecase.DeletePrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.ExtractPrescriptionUseCase
import com.medsy.domain.prescription.usecase.ImportPrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.PreparePrescriptionCaptureUseCase
import com.medsy.domain.prescription.usecase.SearchPrescriptionMedicinesUseCase
import com.medsy.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prepareCapture: PreparePrescriptionCaptureUseCase,
    private val importImage: ImportPrescriptionImageUseCase,
    private val deleteImage: DeletePrescriptionImageUseCase,
    private val extractPrescription: ExtractPrescriptionUseCase,
    private val searchMedicines: SearchPrescriptionMedicinesUseCase,
    private val addPrescriptionToCart: AddPrescriptionToCartUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PrescriptionState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PrescriptionUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var pendingCameraImage: PrescriptionImage? = null
    private var extractionJob: Job? = null
    private var searchJob: Job? = null

    fun onIntent(intent: PrescriptionUIIntent) {
        when (intent) {
            PrescriptionUIIntent.BackClicked -> handleBack()
            PrescriptionUIIntent.CameraClicked -> prepareCamera()
            PrescriptionUIIntent.GalleryClicked -> sendEffect(PrescriptionUIEffect.LaunchGallery)
            is PrescriptionUIIntent.CameraCaptureCompleted -> handleCameraResult(intent.success)
            is PrescriptionUIIntent.GalleryImageSelected -> handleGalleryResult(intent.uri)
            PrescriptionUIIntent.ChangeImageClicked -> showSourceSelection()
            PrescriptionUIIntent.DeleteImageClicked -> removeCurrentImage()
            PrescriptionUIIntent.ReviewImageClicked -> startExtraction()
            PrescriptionUIIntent.RetryExtractionClicked -> startExtraction()
            PrescriptionUIIntent.ChooseAnotherImageClicked -> showSourceSelection()
            PrescriptionUIIntent.TogglePrescriptionImageClicked ->
                _state.update { it.copy(isPrescriptionExpanded = !it.isPrescriptionExpanded) }

            is PrescriptionUIIntent.ConfirmMedicineClicked -> confirmMedicine(intent.medicineId)
            is PrescriptionUIIntent.EditMedicineClicked -> openMedicinePicker(
                mode = MedicinePickerMode.REPLACE,
                targetMedicineId = intent.medicineId,
                returnStep = PrescriptionStep.MEDICINE_REVIEW,
            )

            is PrescriptionUIIntent.DeleteMedicineClicked -> deleteMedicine(intent.medicineId)
            is PrescriptionUIIntent.IncreaseQuantityClicked -> changeQuantity(intent.medicineId, 1)
            is PrescriptionUIIntent.DecreaseQuantityClicked -> changeQuantity(intent.medicineId, -1)
            PrescriptionUIIntent.AddMedicineManuallyClicked -> openMedicinePicker(
                mode = MedicinePickerMode.ADD,
                targetMedicineId = null,
                returnStep = _state.value.step,
            )

            is PrescriptionUIIntent.MedicineQueryChanged -> search(intent.query)
            is PrescriptionUIIntent.MedicineSelected -> selectMedicine(intent.medicineId)
            PrescriptionUIIntent.AddToCartClicked -> submitPrescription()
            PrescriptionUIIntent.ViewCartClicked -> sendEffect(PrescriptionUIEffect.NavigateCart)
            PrescriptionUIIntent.ReturnHomeClicked -> sendEffect(PrescriptionUIEffect.NavigateHome)
        }
    }

    private fun prepareCamera() {
        if (_state.value.isPreparingImage) return
        viewModelScope.launch {
            _state.update { it.copy(isPreparingImage = true) }
            prepareCapture()
                .onSuccess { image ->
                    pendingCameraImage = image
                    _state.update { it.copy(isPreparingImage = false) }
                    sendEffect(PrescriptionUIEffect.LaunchCamera(image.uri))
                }
                .onFailure { showMediaError() }
        }
    }

    private fun handleCameraResult(success: Boolean) {
        val pending = pendingCameraImage ?: return
        pendingCameraImage = null
        if (success) {
            replaceCurrentImage(pending)
        } else {
            viewModelScope.launch { deleteImage(pending) }
            returnAfterCancelledPicker()
        }
    }

    private fun handleGalleryResult(uri: String?) {
        if (uri == null) {
            returnAfterCancelledPicker()
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isPreparingImage = true) }
            importImage(uri)
                .onSuccess(::replaceCurrentImage)
                .onFailure { showMediaError() }
        }
    }

    private fun replaceCurrentImage(newImage: PrescriptionImage) {
        val previous = _state.value.image
        _state.update {
            it.copy(
                step = PrescriptionStep.IMAGE_PREVIEW,
                image = newImage,
                medicines = emptyList(),
                isPreparingImage = false,
                isPrescriptionExpanded = false,
            )
        }
        if (previous != null && previous != newImage) {
            viewModelScope.launch { deleteImage(previous) }
        }
    }

    private fun removeCurrentImage() {
        val image = _state.value.image
        _state.update { PrescriptionState() }
        if (image != null) viewModelScope.launch { deleteImage(image) }
    }

    private fun showSourceSelection() {
        extractionJob?.cancel()
        _state.update { it.copy(step = PrescriptionStep.SOURCE_SELECTION) }
    }

    private fun returnAfterCancelledPicker() {
        _state.update {
            it.copy(
                step = if (it.image == null) {
                    PrescriptionStep.SOURCE_SELECTION
                } else {
                    PrescriptionStep.IMAGE_PREVIEW
                },
                isPreparingImage = false,
            )
        }
    }

    private fun startExtraction() {
        val image = _state.value.image ?: return
        extractionJob?.cancel()
        extractionJob = viewModelScope.launch {
            _state.update { it.copy(step = PrescriptionStep.EXTRACTING) }
            extractPrescription(image)
                .onSuccess { outcome ->
                    when (outcome) {
                        is PrescriptionExtractionOutcome.MedicinesDetected -> _state.update {
                            it.copy(
                                step = PrescriptionStep.MEDICINE_REVIEW,
                                medicines = outcome.medicines,
                            )
                        }

                        PrescriptionExtractionOutcome.Unreadable ->
                            _state.update { it.copy(step = PrescriptionStep.UNREADABLE) }

                        PrescriptionExtractionOutcome.NoMedicines ->
                            _state.update { it.copy(step = PrescriptionStep.NO_MEDICINES) }
                    }
                }
                .onFailure {
                    _state.update { it.copy(step = PrescriptionStep.UPLOAD_ERROR) }
                }
        }
    }

    private fun confirmMedicine(medicineId: String) {
        _state.update { state ->
            state.copy(
                medicines = state.medicines.map {
                    if (it.medicine.id == medicineId) {
                        it.copy(recognitionStatus = RecognitionStatus.RECOGNIZED)
                    } else {
                        it
                    }
                },
            )
        }
    }

    private fun deleteMedicine(medicineId: String) {
        _state.update { state ->
            state.copy(medicines = state.medicines.filterNot { it.medicine.id == medicineId })
        }
    }

    private fun changeQuantity(medicineId: String, delta: Int) {
        _state.update { state ->
            state.copy(
                medicines = state.medicines.map {
                    if (it.medicine.id == medicineId) {
                        it.copy(quantity = (it.quantity + delta).coerceAtLeast(1))
                    } else {
                        it
                    }
                },
            )
        }
    }

    private fun openMedicinePicker(
        mode: MedicinePickerMode,
        targetMedicineId: String?,
        returnStep: PrescriptionStep,
    ) {
        _state.update {
            it.copy(
                step = PrescriptionStep.MEDICINE_PICKER,
                picker = MedicinePickerState(
                    mode = mode,
                    targetMedicineId = targetMedicineId,
                    returnStep = returnStep,
                    isLoading = true,
                ),
            )
        }
        search("")
    }

    private fun search(query: String) {
        searchJob?.cancel()
        _state.update { it.copy(picker = it.picker.copy(query = query, isLoading = true)) }
        searchJob = viewModelScope.launch {
            searchMedicines(query)
                .onSuccess { medicines ->
                    _state.update {
                        it.copy(
                            picker = it.picker.copy(
                                results = medicines,
                                isLoading = false,
                            ),
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(picker = it.picker.copy(isLoading = false)) }
                    sendEffect(PrescriptionUIEffect.ShowMessage(R.string.prescription_error_generic))
                }
        }
    }

    private fun selectMedicine(medicineId: String) {
        val state = _state.value
        val selected = state.picker.results.firstOrNull { it.id == medicineId } ?: return
        val updated = when (state.picker.mode) {
            MedicinePickerMode.ADD -> addOrIncrement(state.medicines, selected, 1)
            MedicinePickerMode.REPLACE -> replaceMedicine(
                current = state.medicines,
                targetId = state.picker.targetMedicineId,
                replacement = selected,
            )
        }
        _state.update {
            it.copy(
                step = PrescriptionStep.MEDICINE_REVIEW,
                medicines = updated,
            )
        }
    }

    private fun addOrIncrement(
        current: List<PrescriptionMedicine>,
        medicine: Medicine,
        quantity: Int,
    ): List<PrescriptionMedicine> {
        val existing = current.firstOrNull { it.medicine.id == medicine.id }
        return if (existing == null) {
            current + PrescriptionMedicine(
                medicine = medicine,
                quantity = quantity,
                recognitionStatus = RecognitionStatus.RECOGNIZED,
            )
        } else {
            current.map {
                if (it.medicine.id == medicine.id) {
                    it.copy(
                        quantity = it.quantity + quantity,
                        recognitionStatus = RecognitionStatus.RECOGNIZED,
                    )
                } else {
                    it
                }
            }
        }
    }

    private fun replaceMedicine(
        current: List<PrescriptionMedicine>,
        targetId: String?,
        replacement: Medicine,
    ): List<PrescriptionMedicine> {
        val target = current.firstOrNull { it.medicine.id == targetId } ?: return current
        if (replacement.id == target.medicine.id) {
            return current.map {
                if (it.medicine.id == targetId) {
                    it.copy(recognitionStatus = RecognitionStatus.RECOGNIZED)
                } else {
                    it
                }
            }
        }
        return addOrIncrement(
            current = current.filterNot { it.medicine.id == targetId },
            medicine = replacement,
            quantity = target.quantity,
        )
    }

    private fun submitPrescription() {
        val current = _state.value
        val image = current.image ?: return
        if (!current.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            val request = PrescriptionCartRequest(image, current.medicines)
            addPrescriptionToCart(request)
                .onSuccess {
                    _state.update {
                        it.copy(
                            step = PrescriptionStep.CONFIRMATION,
                            isSubmitting = false,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isSubmitting = false) }
                    sendEffect(PrescriptionUIEffect.ShowMessage(R.string.prescription_error_generic))
                }
        }
    }

    private fun handleBack() {
        when (_state.value.step) {
            PrescriptionStep.SOURCE_SELECTION -> {
                if (_state.value.image == null) {
                    sendEffect(PrescriptionUIEffect.NavigateBack)
                } else {
                    _state.update { it.copy(step = PrescriptionStep.IMAGE_PREVIEW) }
                }
            }

            PrescriptionStep.IMAGE_PREVIEW -> _state.update {
                it.copy(step = PrescriptionStep.SOURCE_SELECTION)
            }

            PrescriptionStep.EXTRACTING -> {
                extractionJob?.cancel()
                _state.update { it.copy(step = PrescriptionStep.IMAGE_PREVIEW) }
            }

            PrescriptionStep.MEDICINE_REVIEW,
            PrescriptionStep.UPLOAD_ERROR,
            PrescriptionStep.UNREADABLE,
            PrescriptionStep.NO_MEDICINES,
            -> _state.update { it.copy(step = PrescriptionStep.IMAGE_PREVIEW) }

            PrescriptionStep.MEDICINE_PICKER -> _state.update {
                it.copy(step = it.picker.returnStep)
            }

            PrescriptionStep.CONFIRMATION -> sendEffect(PrescriptionUIEffect.NavigateHome)
        }
    }

    private fun showMediaError() {
        _state.update { it.copy(isPreparingImage = false) }
        sendEffect(PrescriptionUIEffect.ShowMessage(R.string.prescription_media_error))
    }

    private fun sendEffect(effect: PrescriptionUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
