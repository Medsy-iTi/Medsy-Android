package com.medsy.designsystem.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,

    val badgeSuccess: Color,
    val onBadgeSuccess: Color,

    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,

    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,

    // Category Colors
    val categoryContainerBg: Color,
    val onCategoryContainer: Color,
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

    val prescriptionPrimary: Color,
    val prescriptionTitle: Color,
    val prescriptionSupporting: Color,
    val prescriptionSuccessContainer: Color,
    val prescriptionSuccessSoft: Color,
    val prescriptionSuccessContent: Color,
    val prescriptionWarningContainer: Color,
    val prescriptionWarningContent: Color,
    val prescriptionWarningBorder: Color,
    val prescriptionErrorContainer: Color,
    val prescriptionErrorContent: Color,
    val prescriptionDisabledContainer: Color,
    val prescriptionGalleryContainer: Color,
    val prescriptionGalleryContent: Color,
    val prescriptionScanBorder: Color,
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

internal val lightExtendedColors = ExtendedColors(

    success = Color(0xFF166534),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFDCFCE7),
    onSuccessContainer = Color(0xFF14532D),

    badgeSuccess = Color(0xFF00A86B),
    onBadgeSuccess = Color(0xFFFFFFFF),

    warning = Color(0xFF9A5800),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFEDD5),
    onWarningContainer = Color(0xFF7C2D12),

    info = Color(0xFF1D4ED8),
    onInfo = Color(0xFFFFFFFF),
    infoContainer = Color(0xFFDBEAFE),
    onInfoContainer = Color(0xFF1E3A8A),
    categoryContainerBg = Color(0xFFE6FFEE),
    onCategoryContainer = MedsyBrandGreen,

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
    prescriptionPrimary = Color(0xFF0D8653),
    prescriptionTitle = Color(0xFF0D1F17),
    prescriptionSupporting = Color(0xFF6A7D75),
    prescriptionSuccessContainer = Color(0xFFEEF7F2),
    prescriptionSuccessSoft = Color(0xFFE5F4ED),
    prescriptionSuccessContent = Color(0xFF0A6B42),
    prescriptionWarningContainer = Color(0xFFFFFBEB),
    prescriptionWarningContent = Color(0xFF92400E),
    prescriptionWarningBorder = Color(0xFFFDE68A),
    prescriptionErrorContainer = Color(0xFFFEF2F2),
    prescriptionErrorContent = Color(0xFFEF4444),
    prescriptionDisabledContainer = Color(0xFFF1F6F3),
    prescriptionGalleryContainer = Color(0xFFEFF3FF),
    prescriptionGalleryContent = Color(0xFF4F7EF8),
    prescriptionScanBorder = Color(0xFFB3DFC8),
)

internal val darkExtendedColors = ExtendedColors(
    success = Color(0xFF78E29A),
    onSuccess = Color(0xFF00391C),
    successContainer = Color(0xFF0F4D2D),
    onSuccessContainer = Color(0xFFA7F3C2),

    badgeSuccess = Color(0xFF00A86B),
    onBadgeSuccess = Color(0xFFFFFFFF),

    warning = Color(0xFFFFB86C),
    onWarning = Color(0xFF4A2800),
    warningContainer = Color(0xFF5D3A0A),
    onWarningContainer = Color(0xFFFFE0B2),

    info = Color(0xFFAFC6FF),
    onInfo = Color(0xFF082F73),
    infoContainer = Color(0xFF163E86),
    onInfoContainer = Color(0xFFDCE6FF),
    categoryContainerBg = Color(0xFF1D7A4D),
    onCategoryContainer = Color(0xFFE6FFEE),

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
    prescriptionPrimary = Color(0xFF22C55E),
    prescriptionTitle = Color(0xFFF7FAF8),
    prescriptionSupporting = Color(0xFF9CAFA7),
    prescriptionSuccessContainer = Color(0xFF123526),
    prescriptionSuccessSoft = Color(0xFF173F2E),
    prescriptionSuccessContent = Color(0xFF86EFAC),
    prescriptionWarningContainer = Color(0xFF422006),
    prescriptionWarningContent = Color(0xFFFDE68A),
    prescriptionWarningBorder = Color(0xFF854D0E),
    prescriptionErrorContainer = Color(0xFF450A0A),
    prescriptionErrorContent = Color(0xFFFCA5A5),
    prescriptionDisabledContainer = Color(0xFF24332C),
    prescriptionGalleryContainer = Color(0xFF172554),
    prescriptionGalleryContent = Color(0xFF93C5FD),
    prescriptionScanBorder = Color(0xFF365047),
)
