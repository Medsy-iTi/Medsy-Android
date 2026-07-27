package com.medsy.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.home.CategoryUi

@Composable
fun CategoriesSection(
    categories: List<CategoryUi>,
    onViewAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val colors = MaterialTheme.extendedColors
    val categoryColorSchemes = colors.categoryColors

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
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create a list with categories
        val allItems = categories.mapIndexed { index, cat ->
            val colorScheme = categoryColorSchemes[index % categoryColorSchemes.size]
            CategoryItemData(
                id = cat.id,
                name = cat.name,
                bgCol = colorScheme.first,
                iconCol = colorScheme.second,
                icon = Icons.Default.MedicalServices,
                onClick = { onCategoryClick(cat.id) }
            )
        }

        allItems.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    CategoryGridItem(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private data class CategoryItemData(
    val id: String,
    val name: String,
    val bgCol: androidx.compose.ui.graphics.Color,
    val iconCol: androidx.compose.ui.graphics.Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun CategoryGridItem(
    item: CategoryItemData,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { item.onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.1f)
                .shadow(
                    elevation = if (isDark) 12.dp else 6.dp, // Higher elevation in dark mode
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.primary
                )
                .background(item.bgCol, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconCol,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
