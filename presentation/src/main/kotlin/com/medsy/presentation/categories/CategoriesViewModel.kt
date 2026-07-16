package com.medsy.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.R
import com.medsy.presentation.home.CategoryIconType
import com.medsy.presentation.home.CategoryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CategoriesUIState())
    val state: StateFlow<CategoriesUIState> = _state.asStateFlow()

    private val _effect = Channel<CategoriesUIEffect>()
    val effect = _effect.receiveAsFlow()

    private val allCategories = listOf(
        CategoryUi("1", R.string.home_cat_medicine, CategoryIconType.MEDICINE),
        CategoryUi("2", R.string.home_cat_vitamins, CategoryIconType.VITAMINS),
        CategoryUi("3", R.string.home_cat_personal_care, CategoryIconType.PERSONAL_CARE),
        CategoryUi("4", R.string.home_cat_medical_devices, CategoryIconType.MEDICAL_DEVICES),
        CategoryUi("5", R.string.cat_baby_care, CategoryIconType.BABY_CARE),
        CategoryUi("6", R.string.cat_skin_care, CategoryIconType.SKIN_CARE),
        CategoryUi("7", R.string.cat_hair_care, CategoryIconType.HAIR_CARE),
        CategoryUi("8", R.string.cat_daily_essentials, CategoryIconType.DAILY_ESSENTIALS),
    )

    init {
        _state.update {
            it.copy(
                categories = allCategories,
                filteredCategories = allCategories
            )
        }
    }

    fun onIntent(intent: CategoriesUIIntent) {
        when (intent) {
            is CategoriesUIIntent.OnBackClick -> sendEffect(CategoriesUIEffect.NavigateBack)

            is CategoriesUIIntent.OnSearchQueryChange -> {
                val query = intent.query
                val filtered = if (query.isBlank()) {
                    allCategories
                } else {
                    allCategories.filter { cat ->
                        // filtering is done in the UI via stringResource, so we keep all and let UI filter
                        true
                    }
                }
                _state.update {
                    it.copy(
                        searchQuery = query,
                        filteredCategories = filtered
                    )
                }
            }

            is CategoriesUIIntent.OnCategoryClick ->
                sendEffect(CategoriesUIEffect.NavigateToCategory(intent.categoryId))
        }
    }

    private fun sendEffect(effect: CategoriesUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}