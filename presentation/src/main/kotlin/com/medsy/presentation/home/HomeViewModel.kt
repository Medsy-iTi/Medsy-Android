package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.profile.usecase.ObserveProfileUseCase
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val observeProfileUseCase: ObserveProfileUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    private val _effect = Channel<HomeUIEffect>()
    val effect = _effect.receiveAsFlow()

    private var searchSimulationJob: Job? = null

    init {
        _state.update {
            it.copy(
                notificationCount = 1,
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
                categories = emptyList()
            )
        }
        fetchCategories()
        observeProfileData()
        startSearchSimulation()
    }

    private fun observeProfileData() {
        viewModelScope.launch {
            observeProfileUseCase().collectLatest { profile ->
                profile?.homeAddress?.let { address ->
                    _state.update { it.copy(deliveryAddress = address) }
                }
            }
        }
        
        viewModelScope.launch {
            getProfileUseCase().onSuccess { profile ->
                profile.homeAddress?.let { address ->
                    _state.update { it.copy(deliveryAddress = address) }
                }
            }
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

            HomeUIIntent.OnStartSearchSimulation -> startSearchSimulation()
            HomeUIIntent.OnCancelSearchSimulation -> cancelSearchSimulation()
            HomeUIIntent.OnViewOffersClick -> {
                cancelSearchSimulation()
                sendEffect(HomeUIEffect.NavigateToOffers)
            }

            HomeUIIntent.OnSearchWiderRangeClick -> startSearchSimulation()
        }
    }

    private fun startSearchSimulation() {
        searchSimulationJob?.cancel()
        searchSimulationJob = viewModelScope.launch {
            var elapsed = 0
            _state.update {
                it.copy(
                    activeSearchStatus = ActiveSearchStatus.Searching(
                        stage = 1,
                        elapsedTime = elapsed
                    )
                )
            }
            repeat(3) {
                delay(1000)
                elapsed++
                _state.update {
                    it.copy(
                        activeSearchStatus = ActiveSearchStatus.Searching(
                            stage = 1,
                            elapsedTime = elapsed
                        )
                    )
                }
            }

            _state.update {
                it.copy(
                    activeSearchStatus = ActiveSearchStatus.Searching(
                        stage = 2,
                        elapsedTime = elapsed
                    )
                )
            }
            repeat(5) {
                delay(1000)
                elapsed++
                _state.update {
                    it.copy(
                        activeSearchStatus = ActiveSearchStatus.Searching(
                            stage = 2,
                            elapsedTime = elapsed
                        )
                    )
                }
            }

            _state.update {
                it.copy(
                    activeSearchStatus = ActiveSearchStatus.Searching(
                        stage = 3,
                        elapsedTime = elapsed
                    )
                )
            }
            repeat(6) {
                delay(1000)
                elapsed++
                _state.update {
                    it.copy(
                        activeSearchStatus = ActiveSearchStatus.Searching(
                            stage = 3,
                            elapsedTime = elapsed
                        )
                    )
                }
            }

            _state.update {
                it.copy(
                    activeSearchStatus = ActiveSearchStatus.FirstOfferArrived(
                        elapsedTime = elapsed,
                        minPrice = 48
                    )
                )
            }
            repeat(3) {
                delay(1000)
                elapsed++
                _state.update {
                    it.copy(
                        activeSearchStatus = ActiveSearchStatus.FirstOfferArrived(
                            elapsedTime = elapsed,
                            minPrice = 48
                        )
                    )
                }
            }

            _state.update {
                it.copy(
                    activeSearchStatus = ActiveSearchStatus.MultipleOffersArrived(
                        elapsedTime = elapsed,
                        minPrice = 36,
                        totalOffers = 3
                    )
                )
            }
            repeat(3) {
                delay(1000)
                elapsed++
                _state.update {
                    it.copy(
                        activeSearchStatus = ActiveSearchStatus.MultipleOffersArrived(
                            elapsedTime = elapsed,
                            minPrice = 36,
                            totalOffers = 3
                        )
                    )
                }
            }

            _state.update { it.copy(activeSearchStatus = ActiveSearchStatus.SearchEndedNoOffers) }
        }
    }

    private fun cancelSearchSimulation() {
        searchSimulationJob?.cancel()
        _state.update { it.copy(activeSearchStatus = ActiveSearchStatus.Idle) }
    }

    private fun sendEffect(effect: HomeUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
