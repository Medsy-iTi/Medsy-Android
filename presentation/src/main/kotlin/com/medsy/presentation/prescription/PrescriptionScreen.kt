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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.prescription.components.MedicinePickerScreen
import com.medsy.presentation.prescription.components.PrescriptionConfirmationScreen
import com.medsy.presentation.prescription.components.PrescriptionExtractingScreen
import com.medsy.presentation.prescription.components.PrescriptionImagePreviewScreen
import com.medsy.presentation.prescription.components.PrescriptionNoMedicinesScreen
import com.medsy.presentation.prescription.components.PrescriptionReviewScreen
import com.medsy.presentation.prescription.components.PrescriptionSourceScreen
import com.medsy.presentation.prescription.components.PrescriptionUnreadableScreen
import com.medsy.presentation.prescription.components.PrescriptionUploadErrorScreen

@Composable
fun PrescriptionRoot(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateCart: () -> Unit,
    viewModel: PrescriptionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
        viewModel.effect.collect { effect ->
            when (effect) {
                is PrescriptionUIEffect.LaunchCamera -> cameraLauncher.launch(Uri.parse(effect.uri))
                PrescriptionUIEffect.LaunchGallery -> galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )

                PrescriptionUIEffect.NavigateBack -> onNavigateBack()
                PrescriptionUIEffect.NavigateHome -> onNavigateHome()
                PrescriptionUIEffect.NavigateCart -> onNavigateCart()
                is PrescriptionUIEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(effect.messageRes))
            }
        }
    }

    BackHandler { viewModel.onIntent(PrescriptionUIIntent.BackClicked) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        PrescriptionScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
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
            PrescriptionStep.EXTRACTING -> PrescriptionExtractingScreen(onIntent)
            PrescriptionStep.MEDICINE_REVIEW -> PrescriptionReviewScreen(state, onIntent)
            PrescriptionStep.MEDICINE_PICKER -> MedicinePickerScreen(state.picker, onIntent)
            PrescriptionStep.UPLOAD_ERROR -> PrescriptionUploadErrorScreen(onIntent)
            PrescriptionStep.UNREADABLE -> PrescriptionUnreadableScreen(onIntent)
            PrescriptionStep.NO_MEDICINES -> PrescriptionNoMedicinesScreen(onIntent)
            PrescriptionStep.CONFIRMATION -> PrescriptionConfirmationScreen(state, onIntent)
        }
    }
}
