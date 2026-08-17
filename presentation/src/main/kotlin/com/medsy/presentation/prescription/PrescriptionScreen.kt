package com.medsy.presentation.prescription

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showMessage
import kotlinx.coroutines.flow.collectLatest
import com.medsy.presentation.prescription.medicinepicker.MedicinePickerScreen
import com.medsy.presentation.prescription.prescriptionuploaderror.PrescriptionConfirmationScreen
import com.medsy.presentation.prescription.prescriptionextracting.PrescriptionExtractingScreen
import com.medsy.presentation.prescription.prescriptionimagepreview.PrescriptionImagePreviewScreen
import com.medsy.presentation.prescription.prescriptionuploaderror.PrescriptionNoMedicinesScreen
import com.medsy.presentation.prescription.prescriptionreview.PrescriptionReviewScreen
import com.medsy.presentation.prescription.prescriptionsource.PrescriptionSourceScreen
import com.medsy.presentation.prescription.prescriptionuploaderror.PrescriptionUnreadableScreen
import com.medsy.presentation.prescription.prescriptionuploaderror.PrescriptionUploadErrorScreen

@Composable
fun PrescriptionRoot(
    attachmentOnly: Boolean,
    isMedicineSearch: Boolean = false,
    resultLocalItemId: String? = null,
    resultProductId: Int? = null,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateCart: () -> Unit,
    onPrescriptionAttached: () -> Unit,
    onNavigateToSearch: (String, String) -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    onResultHandled: () -> Unit,
    viewModel: PrescriptionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(attachmentOnly, isMedicineSearch) {
        viewModel.init(attachmentOnly, isMedicineSearch)
    }

    LaunchedEffect(resultLocalItemId, resultProductId) {
        if (resultLocalItemId != null && resultProductId != null) {
            viewModel.onIntent(
                PrescriptionUIIntent.MedicineSelectedFromResult(
                    resultLocalItemId,
                    resultProductId
                )
            )
            onResultHandled()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { viewModel.onIntent(PrescriptionUIIntent.CameraCaptureCompleted(it)) },
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            viewModel.onIntent(PrescriptionUIIntent.GalleryImageSelected(it?.toString()))
        },
    )

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PrescriptionUIEffect.LaunchCamera -> cameraLauncher.launch(Uri.parse(effect.uri))
                PrescriptionUIEffect.LaunchGallery -> galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )

                PrescriptionUIEffect.NavigateBack -> onNavigateBack()
                PrescriptionUIEffect.NavigateHome -> onNavigateHome()
                PrescriptionUIEffect.NavigateCart -> onNavigateCart()
                PrescriptionUIEffect.PrescriptionAttached -> onPrescriptionAttached()
                is PrescriptionUIEffect.NavigateToSearch -> onNavigateToSearch(
                    effect.query,
                    effect.localItemId
                )

                is PrescriptionUIEffect.NavigateToProductDetails -> onNavigateToProductDetails(
                    effect.productId
                )

                is PrescriptionUIEffect.ShowMessage ->
                    snackbarHostState.showMessage(
                        context = context,
                        messageRes = effect.messageRes,
                        isSuccess = effect.isSuccess
                    )
            }
        }
    }

    BackHandler { viewModel.onIntent(PrescriptionUIIntent.BackClicked) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold() { padding ->
            PrescriptionScreen(
                state = state,
                onIntent = viewModel::onIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
        MedsySnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun PrescriptionScreen(
    state: PrescriptionState,
    onIntent: (PrescriptionUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
    ) {
        when (state.step) {
            PrescriptionStep.SOURCE_SELECTION -> PrescriptionSourceScreen(state, onIntent)
            PrescriptionStep.IMAGE_PREVIEW -> PrescriptionImagePreviewScreen(state, onIntent)
            PrescriptionStep.EXTRACTING -> PrescriptionExtractingScreen(state, onIntent)
            PrescriptionStep.MEDICINE_REVIEW -> PrescriptionReviewScreen(state, onIntent)
            PrescriptionStep.MEDICINE_PICKER -> MedicinePickerScreen(state.picker, onIntent)
            PrescriptionStep.UPLOAD_ERROR -> PrescriptionUploadErrorScreen(state, onIntent)
            PrescriptionStep.UNREADABLE -> PrescriptionUnreadableScreen(state, onIntent)
            PrescriptionStep.NO_MEDICINES -> PrescriptionNoMedicinesScreen(state, onIntent)
            PrescriptionStep.CONFIRMATION -> PrescriptionConfirmationScreen(state, onIntent)
        }
    }
}
