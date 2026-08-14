package com.medsy.medsy

import android.os.Bundle
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.medsy.nav.rootnavigation.RootNavDisplay
import com.medsy.data.reminders.platform.ReminderAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val openRemindersRequest = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        openRemindersRequest.value =
            intent?.action == ReminderAlarmScheduler.ACTION_OPEN_REMINDERS

        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.themeMode == null
        }

        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val isDarkTheme = isDarkTheme(state)

            MedsyTheme(darkTheme = isDarkTheme) {
                RootNavDisplay(
                    openRemindersRequest = openRemindersRequest.value,
                    onOpenRemindersRequestConsumed = {
                        openRemindersRequest.value = false
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == ReminderAlarmScheduler.ACTION_OPEN_REMINDERS) {
            openRemindersRequest.value = true
        }
    }

    @Composable
    private fun isDarkTheme(state: MainState): Boolean =
        when (state.themeMode) {
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
            ThemeMode.System -> isSystemInDarkTheme()
            null -> isSystemInDarkTheme()
        }
}
