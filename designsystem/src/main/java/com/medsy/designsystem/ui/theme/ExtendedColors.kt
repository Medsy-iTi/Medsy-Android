package com.medsy.designsystem.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    // Category Colors
    val categoryMedicineBg: Color,
    val categoryMedicineIcon: Color,
    val categoryVitaminsBg: Color,
    val categoryVitaminsIcon: Color,
    val categoryPersonalCareBg: Color,
    val categoryPersonalCareIcon: Color,
    val categoryMedicalDevicesBg: Color,
    val categoryMedicalDevicesIcon: Color,
    val categoryBabyCareBg: Color,
    val categoryBabyCareIcon: Color,
    val categorySkinCareBg: Color,
    val categorySkinCareIcon: Color,
    val categoryHairCareBg: Color,
    val categoryHairCareIcon: Color,
    val categoryDailyEssentialsBg: Color,
    val categoryDailyEssentialsIcon: Color,
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
    categoryMedicineBg = Color(0xFFE0E7FF),
    categoryMedicineIcon = Color(0xFF3B82F6),
    categoryVitaminsBg = Color(0xFFFFEDD5),
    categoryVitaminsIcon = Color(0xFFF97316),
    categoryPersonalCareBg = Color(0xFFFCE7F3),
    categoryPersonalCareIcon = Color(0xFFEC4899),
    categoryMedicalDevicesBg = Color(0xFFF3E8FF),
    categoryMedicalDevicesIcon = Color(0xFF8B5CF6),
    categoryBabyCareBg = Color(0xFFE0F2FE),
    categoryBabyCareIcon = Color(0xFF0EA5E9),
    categorySkinCareBg = Color(0xFFDCFCE7),
    categorySkinCareIcon = Color(0xFF22C55E),
    categoryHairCareBg = Color(0xFFFEF9C3),
    categoryHairCareIcon = Color(0xFFEAB308),
    categoryDailyEssentialsBg = Color(0xFFF3F4F6),
    categoryDailyEssentialsIcon = Color(0xFF6B7280),
    categoryMoreBg = Color(0xFFF3F4F6),
    categoryMoreIcon = Color(0xFF6B7280),
)

internal val darkExtendedColors = ExtendedColors(
    categoryMedicineBg = Color(0xFFE0E7FF),
    categoryMedicineIcon = Color(0xFF3B82F6),
    categoryVitaminsBg = Color(0xFFFFEDD5),
    categoryVitaminsIcon = Color(0xFFF97316),
    categoryPersonalCareBg = Color(0xFFFCE7F3),
    categoryPersonalCareIcon = Color(0xFFEC4899),
    categoryMedicalDevicesBg = Color(0xFFF3E8FF),
    categoryMedicalDevicesIcon = Color(0xFF8B5CF6),
    categoryBabyCareBg = Color(0xFFE0F2FE),
    categoryBabyCareIcon = Color(0xFF0EA5E9),
    categorySkinCareBg = Color(0xFFDCFCE7),
    categorySkinCareIcon = Color(0xFF22C55E),
    categoryHairCareBg = Color(0xFFFEF9C3),
    categoryHairCareIcon = Color(0xFFEAB308),
    categoryDailyEssentialsBg = Color(0xFFF3F4F6),
    categoryDailyEssentialsIcon = Color(0xFF6B7280),
    categoryMoreBg = Color(0xFFF3F4F6),
    categoryMoreIcon = Color(0xFF6B7280),
)