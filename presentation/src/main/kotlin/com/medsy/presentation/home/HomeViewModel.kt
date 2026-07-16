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
import kotlin.math.exp

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    private val _effect = Channel<HomeUIEffect>()
    val effect = _effect.receiveAsFlow()

    init {

        _state.update {
            it.copy(
                notificationCount = 1,
                deliveryAddress = "شارع النيل، المعادي",
                banners = listOf(
                    PromoBannerUi(
                        id = "1",
                        titleRes = R.string.home_promo_title_one,
                        subtitleRes = R.string.home_promo_subtitle_one,
                        extraTitleRes = R.string.home_promo_extratext_one,
                        imageRes = R.drawable.banner1,
                        imageContentDescRes = R.string.home_banner_image_desc_one
                    ),
                    PromoBannerUi(
                        id = "2",
                        titleRes = R.string.home_promo_title_two,
                        subtitleRes = R.string.home_promo_subtitle_two,
                        extraTitleRes = R.string.home_promo_extratext_two,
                        imageRes = R.drawable.banner2,
                        imageContentDescRes = R.string.home_banner_image_desc_two
                    ),
                    PromoBannerUi(
                        id = "3",
                        titleRes = R.string.home_promo_title_three,
                        subtitleRes = R.string.home_promo_subtitle_three,
                        extraTitleRes = R.string.home_promo_extratext_three,
                        imageRes = R.drawable.banner3,
                        imageContentDescRes = R.string.home_banner_image_desc_three
                    ),
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

    fun onIntent(intent: HomeUIIntent) {
        when (intent) {
            HomeUIIntent.OnSearchFieldClick -> {
                sendEffect(HomeUIEffect.NavigateToSearch)
            }

            HomeUIIntent.OnSearchMedicineClick -> {
                sendEffect(HomeUIEffect.NavigateToSearch)
            }

            HomeUIIntent.OnUploadPrescriptionClick -> sendEffect(HomeUIEffect.NavigateToUploadPrescription)
            HomeUIIntent.OnNotificationClick -> sendEffect(HomeUIEffect.NavigateToNotifications)
            HomeUIIntent.OnAddressClick -> sendEffect(HomeUIEffect.NavigateToAddressSelection)
            HomeUIIntent.OnPromoClick -> {}
            HomeUIIntent.OnViewAllCategoriesClick -> sendEffect(HomeUIEffect.NavigateToCategories)
            is HomeUIIntent.OnCategoryClick -> sendEffect(HomeUIEffect.NavigateToCategory(intent.categoryId))
        }
    }

    private fun sendEffect(effect: HomeUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
