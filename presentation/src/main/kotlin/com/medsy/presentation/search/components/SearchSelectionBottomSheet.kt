package com.medsy.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchSelectionBottomSheet(
    titleRes: Int,
    options: List<T>,
    selectedOption: T,
    optionLabelRes: (T) -> Int,
    onOptionClick: (T) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchSelectionBottomSheet(
        titleRes = titleRes,
        options = options,
        selectedOption = selectedOption,
        optionLabel = { stringResource(optionLabelRes(it)) },
        onOptionClick = onOptionClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    )
}

/**
 * Same selection bottom sheet, for options whose label is a dynamic string
 * rather than a fixed string resource (e.g. a category name from the API).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchSelectionBottomSheet(
    titleRes: Int,
    options: List<T>,
    selectedOption: T,
    optionLabel: @Composable (T) -> String,
    onOptionClick: (T) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 22.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            options.forEach { option ->
                val isSelected = option == selectedOption
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                        .clickable { onOptionClick(option) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onOptionClick(option) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = optionLabel(option),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
