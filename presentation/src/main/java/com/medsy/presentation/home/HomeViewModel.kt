package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.R
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
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    private val _event = Channel<HomeUIEffect>()
    val event = _event.receiveAsFlow()

    init {

        _state.update {
            it.copy(
                notificationCount = 1,
                deliveryAddress = "شارع النيل، المعادي",
                banners = listOf(
                    PromoBannerUi(
                        id = "1",
                        titleRes = R.string.home_promo_title,
                        subtitleRes = R.string.home_promo_subtitle,
                        discountTextRes = R.string.home_promo_desc,
                        ctaTextRes = R.string.home_promo_button
                    ),
                    PromoBannerUi(
                        id = "2",
                        titleRes = R.string.home_promo_title,
                        subtitleRes = R.string.home_promo_subtitle,
                        discountTextRes = R.string.home_promo_desc,
                        ctaTextRes = R.string.home_promo_button
                    ),
                    PromoBannerUi(
                        id = "3",
                        titleRes = R.string.home_promo_title,
                        subtitleRes = R.string.home_promo_subtitle,
                        discountTextRes = R.string.home_promo_desc,
                        ctaTextRes = R.string.home_promo_button
                    ),
                    PromoBannerUi(
                        id = "4",
                        titleRes = R.string.home_promo_title,
                        subtitleRes = R.string.home_promo_subtitle,
                        discountTextRes = R.string.home_promo_desc,
                        ctaTextRes = R.string.home_promo_button
                    )
                ),
                categories = listOf(
                    CategoryUi("1", R.string.home_cat_medicine, CategoryIconType.MEDICINE),
                    CategoryUi("2", R.string.home_cat_vitamins, CategoryIconType.VITAMINS),
                    CategoryUi(
                        "3",
                        R.string.home_cat_personal_care,
                        CategoryIconType.PERSONAL_CARE
                    ),
                    CategoryUi(
                        "4",
                        R.string.home_cat_medical_devices,
                        CategoryIconType.MEDICAL_DEVICES
                    ),
                    CategoryUi("5", R.string.home_cat_more, CategoryIconType.MORE)
                )
            )
        }
    }

    fun onAction(action: HomeUIIntent) {
        when (action) {
            HomeUIIntent.OnSearchFieldClick -> {
                sendEvent(HomeUIEffect.NavigateToSearch)
            }

            HomeUIIntent.OnSearchMedicineClick -> {
                sendEvent(HomeUIEffect.NavigateToSearch)
            }

            HomeUIIntent.OnUploadPrescriptionClick -> sendEvent(HomeUIEffect.NavigateToUploadPrescription)
            HomeUIIntent.OnNotificationClick -> sendEvent(HomeUIEffect.NavigateToNotifications)
            HomeUIIntent.OnAddressClick -> sendEvent(HomeUIEffect.NavigateToAddressSelection)
            HomeUIIntent.OnPromoClick -> {}
            HomeUIIntent.OnViewAllCategoriesClick -> sendEvent(HomeUIEffect.NavigateToCategories)
            is HomeUIIntent.OnCategoryClick -> sendEvent(HomeUIEffect.NavigateToCategory(action.categoryId))
        }
    }

    private fun sendEvent(event: HomeUIEffect) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
