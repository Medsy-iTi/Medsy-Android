package com.medsy.presentation.prescription.medicinepicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.prescription.model.Medicine
import com.medsy.presentation.R
import com.medsy.presentation.prescription.MedicinePickerState
import com.medsy.presentation.prescription.PrescriptionUIIntent
import com.medsy.presentation.prescription.components.PrescriptionAppBar
import com.medsy.presentation.search.components.ProductResultCard
import com.medsy.presentation.search.components.SearchFilterChips
import com.medsy.presentation.search.components.SearchInputBar
import com.medsy.presentation.search.components.SearchResultsHeader

@Composable
fun MedicinePickerScreen(
    state: MedicinePickerState,
    onIntent: (PrescriptionUIIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PrescriptionAppBar(
            title = stringResource(R.string.search_results_title),
            onBack = { onIntent(PrescriptionUIIntent.BackClicked) },
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SearchInputBar(
                query = state.query,
                onQueryChange = { onIntent(PrescriptionUIIntent.MedicineQueryChanged(it)) },
                onClearClick = { onIntent(PrescriptionUIIntent.MedicineQueryChanged("")) },
                modifier = Modifier.padding(top = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                SearchFilterChips(
                    chips = emptyList(),
                    onChipClick = {},
                )
            }

            SearchResultsHeader(
                resultsCount = state.results.size,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            )
        }

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            state.results.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.prescription_no_search_results),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> {
                val groupedResults = remember(state.results) {
                    state.results.groupBy { it.name.split(" ").first().uppercase() }
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedResults.forEach { (brand, products) ->
                        item {
                            GroupedMedicineSection(
                                brand = brand,
                                products = products,
                                onSelect = { onIntent(PrescriptionUIIntent.MedicineSelected(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupedMedicineSection(
    brand: String,
    products: List<Medicine>,
    onSelect: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(products.size == 1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brand,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.search_results_count, products.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                products.forEach { medicine ->
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    ProductResultCard(
                        name = medicine.name,
                        subtitle = medicine.strength ?: "",
                        imageUrl = medicine.imageUrl,
                        priceEgp = medicine.price,
                        isFavorite = false,
                        onClick = { onSelect(medicine.productId) },
                        onFavoriteClick = {},
                        onAddToCartClick = { onSelect(medicine.productId) },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(Color.Transparent)
                            .border(0.dp, Color.Transparent)
                    )
                }
            }
        }
    }
}
