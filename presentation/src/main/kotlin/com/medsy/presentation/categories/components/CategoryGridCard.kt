package com.medsy.presentation.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.home.CategoryIconType
import com.medsy.presentation.home.CategoryUi
import com.medsy.presentation.R

@Composable
fun CategoryGridCard(
    category: CategoryUi,
    productCount: Int,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.extendedColors

    val (icon, bgColor, iconColor) = when (category.iconType) {
        CategoryIconType.MEDICINE -> Triple(
            Icons.Default.Medication,
            colors.categoryMedicineBg,
            colors.categoryMedicineIcon
        )

        CategoryIconType.VITAMINS -> Triple(
            Icons.Default.HealthAndSafety,
            colors.categoryVitaminsBg,
            colors.categoryVitaminsIcon
        )

        CategoryIconType.PERSONAL_CARE -> Triple(
            Icons.Default.Face,
            colors.categoryPersonalCareBg,
            colors.categoryPersonalCareIcon
        )

        CategoryIconType.MEDICAL_DEVICES -> Triple(
            Icons.Default.MedicalServices,
            colors.categoryMedicalDevicesBg,
            colors.categoryMedicalDevicesIcon
        )

        CategoryIconType.BABY_CARE -> Triple(
            Icons.Default.BabyChangingStation,
            colors.categoryBabyCareBg,
            colors.categoryBabyCareIcon
        )

        CategoryIconType.SKIN_CARE -> Triple(
            Icons.Default.BlurOn,
            colors.categorySkinCareBg,
            colors.categorySkinCareIcon
        )

        CategoryIconType.HAIR_CARE -> Triple(
            Icons.Default.ContentCut,
            colors.categoryHairCareBg,
            colors.categoryHairCareIcon
        )

        CategoryIconType.DAILY_ESSENTIALS -> Triple(
            Icons.Default.ShoppingBasket,
            colors.categoryDailyEssentialsBg,
            colors.categoryDailyEssentialsIcon
        )

        CategoryIconType.MORE -> Triple(
            Icons.Default.MoreHoriz,
            colors.categoryMoreBg,
            colors.categoryMoreIcon
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(bgColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(category.nameRes),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "$productCount Products",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}
