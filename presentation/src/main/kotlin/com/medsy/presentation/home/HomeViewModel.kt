package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.requests.usecase.ObserveActiveRequestsWithStatusUseCase
import com.medsy.domain.requests.usecase.RemoveActiveRequestUseCase
import com.medsy.domain.requests.model.ActiveRequestStatus
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val observeActiveRequestsWithStatusUseCase: ObserveActiveRequestsWithStatusUseCase,
    private val removeActiveRequestUseCase: RemoveActiveRequestUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    private val _effect = Channel<HomeUIEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                notificationCount = 1, deliveryAddress = "", banners = listOf(
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
                ), categories = emptyList()
            )
        }
        fetchCategories()
        preloadProfile()
        observeActiveRequest()
    }

    private fun preloadProfile() {
        viewModelScope.launch {
            getProfileUseCase()
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageRes = null) }
            getCategoriesUseCase(page = 0, size = 20).collectLatest { result ->
                result.onSuccess { domainCategories ->
                    val uiCategories = domainCategories.take(7).map {
                        CategoryUi(
                            id = it.id.toString(), name = it.name
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = null,
                            categories = uiCategories,
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
            }
        }
    }

    private fun observeActiveRequest() {
        viewModelScope.launch {
            observeActiveRequestsWithStatusUseCase().collectLatest { statuses ->
                _state.update { s ->
                    val uiStatuses = statuses.map { domainStatus ->
                        when (domainStatus) {
                            is ActiveRequestStatus.Searching -> ActiveSearchStatus.Searching(
                                requestId = domainStatus.requestId,
                                remainingTimeSeconds = domainStatus.remainingTimeSeconds
                            )
                            is ActiveRequestStatus.FirstOfferArrived -> ActiveSearchStatus.FirstOfferArrived(
                                requestId = domainStatus.requestId,
                                remainingTimeSeconds = domainStatus.remainingTimeSeconds,
                                minPrice = domainStatus.minPrice,
                                foundCount = domainStatus.foundCount,
                                totalCount = domainStatus.totalCount
                            )
                            is ActiveRequestStatus.MultipleOffersArrived -> ActiveSearchStatus.MultipleOffersArrived(
                                requestId = domainStatus.requestId,
                                remainingTimeSeconds = domainStatus.remainingTimeSeconds,
                                minPrice = domainStatus.minPrice,
                                totalOffers = domainStatus.totalOffers,
                                foundCount = domainStatus.foundCount,
                                totalCount = domainStatus.totalCount
                            )
                        }
                    }
                    s.copy(activeSearchStatuses = uiStatuses)
                }
            }
        }
    }

    fun onIntent(intent: HomeUIIntent) {
        when (intent) {
            HomeUIIntent.OnSearchFieldClick -> {
                sendEffect(HomeUIEffect.NavigateToSearch)
            }

            HomeUIIntent.OnSearchMedicineClick -> {
                sendEffect(HomeUIEffect.NavigateToMedicineImageSearch)
            }

            HomeUIIntent.OnUploadPrescriptionClick -> sendEffect(HomeUIEffect.NavigateToUploadPrescription)
            HomeUIIntent.OnNotificationClick -> sendEffect(HomeUIEffect.NavigateToNotifications)
            HomeUIIntent.OnAddressClick -> sendEffect(HomeUIEffect.NavigateToAddressSelection)
            HomeUIIntent.OnPromoClick -> {}
            HomeUIIntent.OnViewAllCategoriesClick -> sendEffect(HomeUIEffect.NavigateToCategories)
            is HomeUIIntent.OnCategoryClick -> {
                val categoryName =
                    _state.value.categories.find { it.id == intent.categoryId }?.name ?: ""
                sendEffect(HomeUIEffect.NavigateToCategory(intent.categoryId, categoryName))
            }

            HomeUIIntent.OnStartSearchSimulation -> { /* Deprecated */ }
            is HomeUIIntent.OnCancelSearchSimulation -> {
                viewModelScope.launch {
                    removeActiveRequestUseCase(intent.requestId)
                }
            }
            is HomeUIIntent.OnViewOffersClick -> {
                sendEffect(HomeUIEffect.NavigateToOffers(intent.requestId))
            }

            HomeUIIntent.OnSearchWiderRangeClick -> { /* Refresh/Widen Search */ }
            is HomeUIIntent.OnAddressResolved -> {
                _state.update { it.copy(deliveryAddress = intent.address) }
            }
        }
    }

    private fun sendEffect(effect: HomeUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
