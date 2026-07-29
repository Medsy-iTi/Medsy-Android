package com.medsy.presentation.categories

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.categories.components.CategoriesTopBar
import com.medsy.presentation.categories.components.CategoryGridCard
import com.medsy.presentation.categories.components.CategoriesShimmer
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text


@Composable
fun CategoriesRoot(
    onBackClick: () -> Unit,
    onCategoryClick: (Int, String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoriesUIEffect.NavigateBack -> onBackClick()
                is CategoriesUIEffect.NavigateToCategory -> onCategoryClick(
                    effect.categoryId,
                    effect.categoryName
                )
            }
        }
    }

    CategoriesScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun CategoriesScreen(
    state: CategoriesUIState,
    onIntent: (CategoriesUIIntent) -> Unit
) {
    if (state.isLoading) {
        CategoriesShimmer()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
        ) {
            CategoriesTopBar(
                onBackClick = { onIntent(CategoriesUIIntent.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onIntent(CategoriesUIIntent.OnSearchQueryChange(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.categories_search_hint),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.filteredCategories.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))
                    val progress by animateLottieCompositionAsState(
                        composition,
                        iterations = LottieConstants.IterateForever
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(250.dp)
                    )
                }
            } else {
                val categoryColorSchemes = MaterialTheme.extendedColors.categoryColors

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(state.filteredCategories) { index, category ->
                        val colorScheme = categoryColorSchemes[index % categoryColorSchemes.size]
                        CategoryGridCard(
                            category = category,
                            containerColor = colorScheme.first,
                            contentColor = colorScheme.second,
                            borderColor = colorScheme.second.copy(alpha = 0.3f),
                            onClick = { onIntent(CategoriesUIIntent.OnCategoryClick(category.id)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}


