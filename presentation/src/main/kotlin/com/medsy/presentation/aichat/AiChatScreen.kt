package com.medsy.presentation.aichat

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showInfo
import com.medsy.designsystem.components.showMessage
import com.medsy.presentation.R
import kotlinx.coroutines.flow.collectLatest
import com.medsy.presentation.aichat.components.AiChatComposer
import com.medsy.presentation.aichat.components.AiChatConversation
import com.medsy.presentation.aichat.components.AiChatNewChatDialog
import com.medsy.presentation.aichat.components.AiChatTopBar
import com.medsy.presentation.aichat.components.AiChatTypingIndicator
import com.medsy.presentation.aichat.components.AiChatWelcome
import java.util.Locale

@Composable
fun AiChatRoot(
    initialPrompt: String?,
    onNavigateBack: () -> Unit,
    onOpenProduct: (Int) -> Unit,
    onOpenCategory: (Int, String) -> Unit,
    onOpenCartTab: () -> Unit,
    onOpenCartRequest: () -> Unit,
    onDial: (String) -> Unit,
    viewModel: AiChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(initialPrompt) {
        viewModel.setInitialPrompt(initialPrompt)
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val text = if (result.resultCode == Activity.RESULT_OK) {
            result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
        } else {
            null
        }
        viewModel.onIntent(AiChatUIIntent.VoiceResult(text))
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { viewModel.onIntent(AiChatUIIntent.CameraCaptureCompleted(it)) },
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            viewModel.onIntent(AiChatUIIntent.GalleryImageSelected(it?.toString()))
        },
    )

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AiChatUIEffect.LaunchVoiceInput -> try {
                    voiceLauncher.launch(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                            )
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE,
                                Locale.getDefault().toLanguageTag(),
                            )
                            putExtra(
                                RecognizerIntent.EXTRA_PROMPT,
                                ContextCompat.getString(context, R.string.ai_chat_voice_prompt),
                            )
                        }
                    )
                } catch (_: ActivityNotFoundException) {
                    snackbarHostState.showInfo(
                        ContextCompat.getString(context, R.string.ai_chat_voice_unavailable)
                    )
                }

                is AiChatUIEffect.LaunchCamera ->
                    cameraLauncher.launch(Uri.parse(effect.uri))

                AiChatUIEffect.LaunchGallery -> galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )

                is AiChatUIEffect.OpenProduct -> onOpenProduct(effect.productId)
                is AiChatUIEffect.OpenCategory ->
                    onOpenCategory(effect.categoryId, effect.categoryName)

                is AiChatUIEffect.DialNumber -> try {
                    onDial(effect.number)
                } catch (_: ActivityNotFoundException) {
                    snackbarHostState.showInfo(
                        ContextCompat.getString(context, R.string.ai_chat_dialer_unavailable)
                    )
                }

                AiChatUIEffect.NavigateToCartTab -> onOpenCartTab()
                AiChatUIEffect.NavigateToCartRequest -> onOpenCartRequest()
                is AiChatUIEffect.ShowMessage -> snackbarHostState.showMessage(
                    context = context,
                    messageRes = effect.messageRes,
                    isSuccess = effect.isSuccess
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AiChatScreen(
            state = state,
            onNavigateBack = onNavigateBack,
            onIntent = viewModel::onIntent,
        )
        MedsySnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    state: AiChatState,
    onNavigateBack: () -> Unit,
    onIntent: (AiChatUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AiChatTopBar(
                hasMessages = state.messages.isNotEmpty(),
                onBack = onNavigateBack,
                onNewChat = { onIntent(AiChatUIIntent.NewChatClicked) },
            )
        },
        bottomBar = {
            AiChatComposer(
                value = state.input,
                enabled = state.canSend,
                pendingAttachment = state.pendingAttachment,
                onValueChange = { onIntent(AiChatUIIntent.InputChanged(it)) },
                onAttach = { onIntent(AiChatUIIntent.AttachClicked) },
                onRemoveAttachment = { onIntent(AiChatUIIntent.RemoveAttachmentClicked) },
                onVoice = { onIntent(AiChatUIIntent.VoiceClicked) },
                onSend = { onIntent(AiChatUIIntent.SendClicked) },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
        ) {
            when {
                state.isLoadingHistory -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    AiChatTypingIndicator()
                }

                state.historyErrorRes != null -> HistoryErrorCard(
                    messageRes = state.historyErrorRes,
                    onRetry = { onIntent(AiChatUIIntent.RetryHistory) },
                )

                state.messages.isEmpty() -> AiChatWelcome(onIntent = onIntent)
                else -> AiChatConversation(state = state, onIntent = onIntent)
            }
        }
    }

    if (state.isAttachSheetVisible) {
        AttachSourceSheet(onIntent = onIntent)
    }

    if (state.isNewChatDialogVisible) {
        AiChatNewChatDialog(
            onDismiss = { onIntent(AiChatUIIntent.DismissNewChat) },
            onConfirm = { onIntent(AiChatUIIntent.ConfirmNewChat) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachSourceSheet(onIntent: (AiChatUIIntent) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onIntent(AiChatUIIntent.AttachSheetDismissed) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.ai_chat_attach_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp),
            )
            AttachSourceOption(
                icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = null) },
                label = stringResource(R.string.ai_chat_attach_camera),
                onClick = { onIntent(AiChatUIIntent.CameraClicked) },
            )
            AttachSourceOption(
                icon = { Icon(Icons.Outlined.PhotoLibrary, contentDescription = null) },
                label = stringResource(R.string.ai_chat_attach_gallery),
                onClick = { onIntent(AiChatUIIntent.GalleryClicked) },
            )
        }
    }
}

@Composable
private fun AttachSourceOption(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Spacer(Modifier.width(12.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun HistoryErrorCard(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.ai_chat_history_error_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(messageRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.ai_chat_history_retry))
                }
            }
        }
    }
}
