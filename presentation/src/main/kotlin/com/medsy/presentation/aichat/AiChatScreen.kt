package com.medsy.presentation.aichat

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.medsy.presentation.R
import com.medsy.presentation.aichat.components.AiChatComposer
import com.medsy.presentation.aichat.components.AiChatConversation
import com.medsy.presentation.aichat.components.AiChatNewChatDialog
import com.medsy.presentation.aichat.components.AiChatTopBar
import com.medsy.presentation.aichat.components.AiChatWelcome
import java.util.Locale

@Composable
fun AiChatRoot(
    onNavigateBack: () -> Unit,
    onOpenProduct: (Int) -> Unit,
    viewModel: AiChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
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
                    snackbarHostState.showSnackbar(
                        ContextCompat.getString(context, R.string.ai_chat_voice_unavailable)
                    )
                }

                is AiChatUIEffect.OpenProduct -> onOpenProduct(effect.productId)
                is AiChatUIEffect.ShowMessage -> snackbarHostState.showSnackbar(
                    ContextCompat.getString(context, effect.messageRes)
                )
            }
        }
    }

    AiChatScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun AiChatScreen(
    state: AiChatState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onIntent: (AiChatUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                enabled = !state.isResponding,
                onValueChange = { onIntent(AiChatUIIntent.InputChanged(it)) },
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
            if (state.messages.isEmpty()) {
                AiChatWelcome(onIntent = onIntent)
            } else {
                AiChatConversation(state = state, onIntent = onIntent)
            }
        }
    }

    if (state.isNewChatDialogVisible) {
        AiChatNewChatDialog(
            onDismiss = { onIntent(AiChatUIIntent.DismissNewChat) },
            onConfirm = { onIntent(AiChatUIIntent.ConfirmNewChat) },
        )
    }
}
