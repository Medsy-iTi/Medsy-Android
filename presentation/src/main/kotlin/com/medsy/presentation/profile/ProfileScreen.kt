package com.medsy.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.common.preferences.model.AppLanguage
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.presentation.R
import com.medsy.presentation.profile.components.ProfileHeaderCard
import com.medsy.presentation.profile.components.ProfileLogoutBottomSheet
import com.medsy.presentation.profile.components.ProfileMenuItem
import com.medsy.presentation.profile.components.ProfileMenuSection
import com.medsy.presentation.profile.components.ProfileSelectionBottomSheet
import com.medsy.presentation.profile.components.ProfileSelectionOption

@Composable
fun ProfileRoot(
    onNavigateToPersonalDetails: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileUIEffect.NavigateToPersonalDetails -> onNavigateToPersonalDetails()
                ProfileUIEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    ProfileScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileUIIntent) -> Unit,
) {

    val currentAppearance = stringResource(
        when (state.themeMode) {
            ThemeMode.System -> R.string.profile_appearance_system_default
            ThemeMode.Light -> R.string.profile_appearance_light
            ThemeMode.Dark -> R.string.profile_appearance_dark
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    )
    {


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 30.dp,
                end = 20.dp,
                bottom = 36.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.profile_title),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }
            item {
                ProfileHeaderCard(
                    name = state.name,
                    image = state.image,
                    phoneNumber = state.phoneNumber,
                    isVerified = state.isVerified,
                )
            }

            item {
                ProfileMenuSection(
                    title = stringResource(R.string.profile_section_account),
                    items = listOf(
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_personal_details),
                            subtitle = stringResource(R.string.profile_personal_details_subtitle),
                            icon = Icons.Outlined.Person,
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = { onIntent(ProfileUIIntent.PersonalDetailsClicked) },
                        ),
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_notifications),
                            subtitle = stringResource(R.string.profile_notifications_subtitle),
                            icon = Icons.Outlined.Notifications,
                            iconTint = MaterialTheme.extendedColors.blueContent,
                            onClick = {},
                        ),
                    ),
                )
            }

            item {
                ProfileMenuSection(
                    title = stringResource(R.string.profile_section_preferences),
                    items = listOf(
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_language),
                            value = getCurrentLanguage(),
                            icon = Icons.Outlined.Language,
                            iconTint = MaterialTheme.extendedColors.orangeContent,
                            onClick = { onIntent(ProfileUIIntent.LanguageClicked) },
                        ),
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_appearance),
                            value = currentAppearance,
                            icon = Icons.Outlined.DarkMode,
                            iconTint = MaterialTheme.extendedColors.purpleContent,
                            onClick = { onIntent(ProfileUIIntent.AppearanceClicked) },
                        ),
                    ),
                )
            }

            item {
                ProfileMenuSection(
                    title = stringResource(R.string.profile_section_support),
                    items = listOf(
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_help_center),
                            subtitle = stringResource(R.string.profile_help_center_subtitle),
                            icon = Icons.AutoMirrored.Outlined.HelpOutline,
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = {},
                        ),
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_contact_us),
                            icon = Icons.Outlined.SupportAgent,
                            iconTint = MaterialTheme.extendedColors.pinkContent,
                            onClick = {},
                        ),
                        ProfileMenuItem(
                            title = stringResource(R.string.profile_terms),
                            icon = Icons.Outlined.Description,
                            iconTint = MaterialTheme.extendedColors.neutralContent,
                            onClick = {},
                        ),
                    ),
                )
            }

            item {
                OutlinedButton(
                    onClick = { onIntent(ProfileUIIntent.LogoutClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.72f),
                    ),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.06f),
                    ),
                    contentPadding = PaddingValues(vertical = 15.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.profile_logout),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }

    when (state.activeSheet) {
        ProfileSheet.Language -> ProfileSelectionBottomSheet(
            title = stringResource(R.string.profile_language),
            options = listOf(
                ProfileSelectionOption(
                    value = AppLanguage.Arabic,
                    label = stringResource(R.string.profile_language_arabic),
                    selected = isArabic,
                ),
                ProfileSelectionOption(
                    value = AppLanguage.English,
                    label = stringResource(R.string.profile_language_english),
                    selected = !isArabic,
                ),
            ),
            onDismissRequest = { onIntent(ProfileUIIntent.SheetDismissed) },
            onOptionClick = { language ->
                onIntent(ProfileUIIntent.LanguageSelected(language))
            },
        )

        ProfileSheet.Appearance -> ProfileSelectionBottomSheet(
            title = stringResource(R.string.profile_appearance),
            options = listOf(
                ProfileSelectionOption(
                    value = ThemeMode.System,
                    label = stringResource(R.string.profile_appearance_system_default),
                    selected = state.themeMode == ThemeMode.System,
                ),
                ProfileSelectionOption(
                    value = ThemeMode.Light,
                    label = stringResource(R.string.profile_appearance_light),
                    selected = state.themeMode == ThemeMode.Light,
                ),
                ProfileSelectionOption(
                    value = ThemeMode.Dark,
                    label = stringResource(R.string.profile_appearance_dark),
                    selected = state.themeMode == ThemeMode.Dark,
                ),
            ),
            onDismissRequest = { onIntent(ProfileUIIntent.SheetDismissed) },
            onOptionClick = { themeMode ->
                onIntent(ProfileUIIntent.ThemeSelected(themeMode))
            },
        )

        ProfileSheet.Logout -> ProfileLogoutBottomSheet(
            onDismissRequest = { onIntent(ProfileUIIntent.SheetDismissed) },
            onLogoutClick = { onIntent(ProfileUIIntent.LogoutConfirmed) },
        )

        null -> Unit
    }
}


private val isArabic = Locale.current.language == "ar"

@Composable
private fun getCurrentLanguage(): String = stringResource(
    if (isArabic) R.string.profile_language_arabic else R.string.profile_language_english
)


@Preview(name = "Profile Menu Section")
@Composable
private fun ProfileMenuSectionPreview() {
    Surface {
        ProfileScreen(
            state = ProfileState(
                name = "John Doe",
                image = null,
                phoneNumber = "+1 234 567 890",
                isVerified = true,
            ),
        ) { }

    }
}
