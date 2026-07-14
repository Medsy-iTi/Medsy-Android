package com.medsy.designsystem.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.medsy.designsystem.R

val PlusJakartaSansFontFamily = FontFamily(
    Font(
        resId = R.font.plus_jakarta_sans_light,
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.plus_jakarta_sans_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resId = R.font.plus_jakarta_sans_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resId = R.font.plus_jakarta_sans_bold,
        weight = FontWeight.Bold,
    ),
)


private val baseline = Typography()

val Typography = Typography(
    // Display / headline / title
    displayLarge = baseline.displayLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = PlusJakartaSansFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = PlusJakartaSansFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = PlusJakartaSansFontFamily),
    // Body / label
    bodyLarge = baseline.bodyLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = PlusJakartaSansFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = PlusJakartaSansFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = PlusJakartaSansFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = PlusJakartaSansFontFamily),
)
