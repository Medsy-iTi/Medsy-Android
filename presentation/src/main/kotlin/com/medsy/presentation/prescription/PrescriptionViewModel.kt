package com.medsy.presentation.prescription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.model.AddCartItemsOutcome
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.usecase.AddCartItemsUseCase
import com.medsy.domain.cart.usecase.AttachCartPrescriptionUseCase
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.prescription.model.ExtractedMedicine
import com.medsy.domain.prescription.model.Medicine
import com.medsy.domain.prescription.model.PrescriptionCartRequest
import com.medsy.domain.prescription.model.PrescriptionExtractionOutcome
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.usecase.AddPrescriptionToCartUseCase
import com.medsy.domain.prescription.usecase.DeletePrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.ExtractPrescriptionUseCase
import com.medsy.domain.prescription.usecase.ImportPrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.PreparePrescriptionCaptureUseCase
import com.medsy.domain.prescription.usecase.SearchPrescriptionMedicinesUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prepareCapture: PreparePrescriptionCaptureUseCase,
    private val importImage: ImportPrescriptionImageUseCase,
    private val deleteImage: DeletePrescriptionImageUseCase,
    private val extractPrescription: ExtractPrescriptionUseCase,
    private val searchMedicines: SearchPrescriptionMedicinesUseCase,
    private val addCartItems: AddCartItemsUseCase,
    private val attachCartPrescription: AttachCartPrescriptionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PrescriptionState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PrescriptionUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var pendingCameraImage: PrescriptionImage? = null
    private var extractionJob: Job? = null
    private var searchJob: Job? = null
    private var hasInitialized = false

    fun init(attachmentOnly: Boolean) {
        if (hasInitialized) return
        hasInitialized = true
        _state.update { it.copy(isAttachmentOnly = attachmentOnly) }
    }

    fun onIntent(intent: PrescriptionUIIntent) {
        when (intent) {
            PrescriptionUIIntent.BackClicked -> handleBack()
            PrescriptionUIIntent.CameraClicked -> prepareCamera()
            PrescriptionUIIntent.GalleryClicked -> sendEffect(PrescriptionUIEffect.LaunchGallery)
            is PrescriptionUIIntent.CameraCaptureCompleted -> handleCameraResult(intent.success)
            is PrescriptionUIIntent.GalleryImageSelected -> handleGalleryResult(intent.uri)
            PrescriptionUIIntent.ChangeImageClicked -> showSourceSelection()
            PrescriptionUIIntent.DeleteImageClicked -> removeCurrentImage()
            PrescriptionUIIntent.ReviewImageClicked -> {
                if (_state.value.isAttachmentOnly) {
                    attachPrescriptionOnly()
                } else {
                    startExtraction()
                }
            }
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
                .onError { showMediaError() }
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
                .onError { showMediaError() }
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
        _state.update { PrescriptionState(isAttachmentOnly = it.isAttachmentOnly) }
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
                .onError {
                    _state.update { it.copy(step = PrescriptionStep.UPLOAD_ERROR) }
                }
        }
    }

    private fun confirmMedicine(localItemId: String) {
        _state.update { state ->
            state.copy(
                medicines = state.medicines.map {
                    if (it.localItemId == localItemId) {
                        it.copy(isConfirmed = true)
                    } else {
                        it
                    }
                },
            )
        }
    }

    private fun deleteMedicine(localItemId: String) {
        _state.update { state ->
            state.copy(medicines = state.medicines.filterNot { it.localItemId == localItemId })
        }
    }

    private fun changeQuantity(localItemId: String, delta: Int) {
        _state.update { state ->
            state.copy(
                medicines = state.medicines.map {
                    if (it.localItemId == localItemId) {
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
            if (query.isNotBlank()) delay(SEARCH_DEBOUNCE_MILLIS)
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
                .onError {
                    _state.update { it.copy(picker = it.picker.copy(isLoading = false)) }
                    sendEffect(PrescriptionUIEffect.ShowMessage(R.string.prescription_error_generic))
                }
        }
    }

    private fun selectMedicine(productId: String) {
        val state = _state.value
        val selected =
            state.picker.results.firstOrNull { it.productId.toString() == productId } ?: return
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
        current: List<ExtractedMedicine>,
        medicine: Medicine,
        quantity: Int,
    ): List<ExtractedMedicine> {
        val existing = current.firstOrNull { it.selectedMedicine?.productId == medicine.productId }
        return if (existing == null) {
            current + ExtractedMedicine(
                localItemId = java.util.UUID.randomUUID().toString(),
                rawText = medicine.name,
                extractedName = medicine.name,
                extractedStrength = medicine.strength,
                extractedForm = medicine.form,
                matchStatus = com.medsy.domain.prescription.model.MatchStatus.MATCHED,
                confidence = 1.0,
                candidates = listOf(medicine),
                selectedMedicine = medicine,
                quantity = quantity,
                isConfirmed = true,
            )
        } else {
            current.map {
                if (it.selectedMedicine?.productId == medicine.productId) {
                    it.copy(
                        quantity = it.quantity + quantity,
                        isConfirmed = true,
                    )
                } else {
                    it
                }
            }
        }
    }

    private fun replaceMedicine(
        current: List<ExtractedMedicine>,
        targetId: String?,
        replacement: Medicine,
    ): List<ExtractedMedicine> {
        return current.map {
            if (it.localItemId == targetId) {
                it.copy(
                    selectedMedicine = replacement,
                    isConfirmed = true,
                )
            } else {
                it
            }
        }
    }

    private fun submitPrescription() {
        val current = _state.value
        val image = current.image ?: return
        if (!current.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            val items = current.medicines.map {
                CartItemInput(
                    productId = it.medicine.id,
                    quantity = it.quantity,
                )
            }
            when (val result = addCartItems(items)) {
                is MedsyResult.Error -> {
                    _state.update { it.copy(isSubmitting = false) }
                    sendEffect(
                        PrescriptionUIEffect.ShowMessage(
                            result.error.toMessageRes()
                        )
                    )
                }

                is MedsyResult.Success -> {
                    val isPartial = result.data is AddCartItemsOutcome.Partial
                    val attachmentResult = attachCartPrescription(image)
                    _state.update {
                        it.copy(
                            step = PrescriptionStep.CONFIRMATION,
                            isSubmitting = false,
                            isPartialSubmission = isPartial,
                        )
                    }
                    if (attachmentResult is MedsyResult.Error) {
                        sendEffect(
                            PrescriptionUIEffect.ShowMessage(
                                attachmentResult.error.toMessageRes()
                            )
                        )
                    } else if (isPartial) {
                        sendEffect(
                            PrescriptionUIEffect.ShowMessage(
                                R.string.prescription_partial_cart
                            )
                        )
                    }
                }
            }
        }
    }

    private fun attachPrescriptionOnly() {
        val image = _state.value.image ?: return
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            attachCartPrescription(image)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    sendEffect(PrescriptionUIEffect.PrescriptionAttached)
                }
                .onError { error ->
                    _state.update { it.copy(isSubmitting = false) }
                    sendEffect(PrescriptionUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun handleBack() {
        when (_state.value.step) {
            PrescriptionStep.SOURCE_SELECTION -> {
                sendEffect(PrescriptionUIEffect.NavigateBack)
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

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
