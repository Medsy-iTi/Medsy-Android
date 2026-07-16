package com.medsy.presentation.search.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.medsy.presentation.R

@Composable
fun SearchResultsHeader(
    resultsCount: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.search_results_count, resultsCount),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
