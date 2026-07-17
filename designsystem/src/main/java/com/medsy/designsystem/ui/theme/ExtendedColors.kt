package com.medsy.designsystem.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    // Category Colors

    val blueContainer: Color,
    val blueContent: Color,
    val orangeContainer: Color,
    val orangeContent: Color,
    val pinkContainer: Color,
    val pinkContent: Color,
    val purpleContainer: Color,
    val purpleContent: Color,
    val neutralContainer: Color,
    val neutralContent: Color,

    val categoryMoreBg: Color,
    val categoryMoreIcon: Color,
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current

internal val lightExtendedColors = ExtendedColors(
    blueContainer = Color(0xFFE0E7FF),
    blueContent = Color(0xFF3B82F6),
    orangeContainer = Color(0xFFFFEDD5),
    orangeContent = Color(0xFFF97316),
    pinkContainer = Color(0xFFFCE7F3),
    pinkContent = Color(0xFFEC4899),
    purpleContainer = Color(0xFFF3E8FF),
    purpleContent = Color(0xFF8B5CF6),
    neutralContainer = Color(0xFFF3F4F6),
    neutralContent = Color(0xFF6B7280),


    categoryMoreBg = Color(0xFFF3F4F6),
    categoryMoreIcon = Color(0xFF6B7280),
)

internal val darkExtendedColors = ExtendedColors(
    blueContainer = Color(0xFFE0E7FF),
    blueContent = Color(0xFF3B82F6),
    orangeContainer = Color(0xFFFFEDD5),
    orangeContent = Color(0xFFF97316),
    pinkContainer = Color(0xFFFCE7F3),
    pinkContent = Color(0xFFEC4899),
    purpleContainer = Color(0xFFF3E8FF),
    purpleContent = Color(0xFF8B5CF6),
    neutralContainer = Color(0xFFF3F4F6),
    neutralContent = Color(0xFF6B7280),


    categoryMoreBg = Color(0xFFF3F4F6),
    categoryMoreIcon = Color(0xFF6B7280),
)