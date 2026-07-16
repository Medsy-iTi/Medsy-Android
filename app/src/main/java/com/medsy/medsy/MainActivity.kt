package com.medsy.medsy

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.medsy.nav.rootnavigation.RootNavDisplay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.themeMode == null
        }

        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val isDarkTheme = isDarkTheme(state)

            MedsyTheme(darkTheme = isDarkTheme) {
                RootNavDisplay()
            }
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