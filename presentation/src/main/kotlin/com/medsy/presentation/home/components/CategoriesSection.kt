package com.medsy.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.*
import com.medsy.presentation.R
import com.medsy.presentation.home.CategoryIconType
import com.medsy.presentation.home.CategoryUi

@Composable
fun CategoriesSection(
    categories: List<CategoryUi>,
    onViewAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_section_categories),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.home_view_all),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories.size) { index ->
                val cat = categories[index]

                val (icon, bgCol, iconCol) = when (cat.iconType) {
                    CategoryIconType.MEDICINE -> Triple(
                        Icons.Default.Medication,
                        CategoryMedicineBg,
                        CategoryMedicineIcon
                    )

                    CategoryIconType.VITAMINS -> Triple(
                        Icons.Default.HealthAndSafety,
                        CategoryVitaminsBg,
                        CategoryVitaminsIcon
                    )

                    CategoryIconType.PERSONAL_CARE -> Triple(
                        Icons.Default.Face,
                        CategoryPersonalCareBg,
                        CategoryPersonalCareIcon
                    )

                    CategoryIconType.MEDICAL_DEVICES -> Triple(
                        Icons.Default.MedicalServices,
                        CategoryMedicalDevicesBg,
                        CategoryMedicalDevicesIcon
                    )

                    CategoryIconType.MORE -> Triple(
                        Icons.Default.MoreHoriz,
                        CategoryMoreBg,
                        CategoryMoreIcon
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onCategoryClick(cat.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(bgCol, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconCol,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(cat.nameRes),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
