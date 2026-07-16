package com.medsy.presentation.categories

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySearchBar
import com.medsy.presentation.R
import com.medsy.presentation.categories.components.CategoriesTopBar
import com.medsy.presentation.categories.components.CategoryGridCard

private val categoryProductCount = mapOf(
    "1" to 350,
    "2" to 120,
    "3" to 280,
    "4" to 45,
    "5" to 95,
    "6" to 160,
    "7" to 110,
    "8" to 210,
)

@Composable
fun CategoriesRoot(
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoriesUIEffect.NavigateBack -> onBackClick()
                is CategoriesUIEffect.NavigateToCategory -> onCategoryClick(effect.categoryId)
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
            MedsySearchBar(
                onSearchClick = { /* categories search is read-only tap */ },
                hint = stringResource(R.string.categories_search_hint)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.filteredCategories) { category ->
                CategoryGridCard(
                    category = category,
                    productCount = categoryProductCount[category.id] ?: 0,
                    onClick = { onIntent(CategoriesUIIntent.OnCategoryClick(category.id)) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}


