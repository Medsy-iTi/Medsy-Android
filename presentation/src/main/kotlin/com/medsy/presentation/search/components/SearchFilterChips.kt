package com.medsy.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.search.SearchFilterChipUi

@Composable
fun SearchFilterChips(
    chips: List<SearchFilterChipUi>,
    onChipClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips, key = { it.id }) { chip ->
            SearchFilterChip(chip = chip, onClick = { onChipClick(chip.id) })
        }
    }
}
@Composable
private fun SearchFilterChip(
    chip: SearchFilterChipUi,
    onClick: () -> Unit,
) {
    val backgroundColor =
        if (chip.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val contentColor = if (chip.isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor =
        if (chip.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (chip.hasLeadingIcon) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier,
            )
        }
        Text(
            text = stringResource(chip.labelRes),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (chip.isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
        )
    }
}
