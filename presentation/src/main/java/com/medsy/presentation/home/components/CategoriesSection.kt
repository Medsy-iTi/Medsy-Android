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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medsy.presentation.R
import com.medsy.presentation.home.CategoryIconType
import com.medsy.presentation.home.CategoryUi

@Composable
fun CategoriesSection(
    categories: List<CategoryUi>,
    onViewAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val primaryGreen = Color(0xFF1E7B4D)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_section_categories),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.home_view_all),
                fontSize = 14.sp,
                color = primaryGreen,
                fontWeight = FontWeight.Bold,
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
                        Color(0xFFE0E7FF),
                        Color(0xFF3B82F6)
                    )

                    CategoryIconType.VITAMINS -> Triple(
                        Icons.Default.HealthAndSafety,
                        Color(0xFFFFEDD5),
                        Color(0xFFF97316)
                    )

                    CategoryIconType.PERSONAL_CARE -> Triple(
                        Icons.Default.Face,
                        Color(0xFFFCE7F3),
                        Color(0xFFEC4899)
                    )

                    CategoryIconType.MEDICAL_DEVICES -> Triple(
                        Icons.Default.MedicalServices,
                        Color(0xFFF3E8FF),
                        Color(0xFF8B5CF6)
                    )

                    CategoryIconType.MORE -> Triple(
                        Icons.Default.MoreHoriz,
                        Color(0xFFF3F4F6),
                        Color(0xFF6B7280)
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
