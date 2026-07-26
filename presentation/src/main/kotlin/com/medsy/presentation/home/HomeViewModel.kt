package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.requests.usecase.ObserveActiveRequestUseCase
import com.medsy.domain.requests.usecase.ClearActiveRequestUseCase
import com.medsy.domain.requests.usecase.GetMedicineRequestByIdUseCase
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
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val observeActiveRequestUseCase: ObserveActiveRequestUseCase,
    private val clearActiveRequestUseCase: ClearActiveRequestUseCase,
    private val getOffersForRequestUseCase: com.medsy.domain.offers.usecase.GetOffersForRequestUseCase,
    private val getMedicineRequestByIdUseCase: GetMedicineRequestByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    private val _effect = Channel<HomeUIEffect>()
    val effect = _effect.receiveAsFlow()

    private var countdownJob: Job? = null
    private var mockPollingJob: Job? = null

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
            observeActiveRequestUseCase().collectLatest { request ->
                countdownJob?.cancel()
                mockPollingJob?.cancel()

                if (request == null) {
                    _state.update { it.copy(activeSearchStatus = ActiveSearchStatus.Idle) }
                } else {
                    countdownJob = launch {
                        while (true) {
                            val elapsedMillis = System.currentTimeMillis() - request.createdAtMillis
                            val remainingSeconds = (900 - (elapsedMillis / 1000)).toInt() // 15 minutes = 900 sec

                            if (remainingSeconds <= 0) {
                                clearActiveRequestUseCase()
                                break
                            }

                            _state.update {
                                val currentStatus = it.activeSearchStatus
                                if (currentStatus is ActiveSearchStatus.FirstOfferArrived) {
                                    it.copy(activeSearchStatus = currentStatus.copy(remainingTimeSeconds = remainingSeconds))
                                } else if (currentStatus is ActiveSearchStatus.MultipleOffersArrived) {
                                    it.copy(activeSearchStatus = currentStatus.copy(remainingTimeSeconds = remainingSeconds))
                                } else {
                                    it.copy(
                                        activeSearchStatus = ActiveSearchStatus.Searching(
                                            requestId = request.id,
                                            remainingTimeSeconds = remainingSeconds
                                        )
                                    )
                                }
                            }
                            delay(1000.milliseconds)
                        }
                    }

                    // Real API polling
                    var originalRequestMinPrice = 0
                    val reqResult = getMedicineRequestByIdUseCase(request.id)
                    if (reqResult is com.medsy.domain.common.MedsyResult.Success) {
                        originalRequestMinPrice = reqResult.data.items.sumOf { it.unitPrice * it.quantity }.toInt()
                    }

                    mockPollingJob = launch {
                        while (true) {
                            delay(5000.milliseconds) // Poll every 5 seconds
                            val offersResult = getOffersForRequestUseCase(request.id)
                            if (offersResult is com.medsy.domain.common.MedsyResult.Success) {
                                val offers = offersResult.data.content
                                if (offers.isNotEmpty()) {
                                    val totalCount = offers.maxOfOrNull { it.items.size } ?: 0
                                    val minPrice = originalRequestMinPrice
                                    val maxFoundCount = offers.maxOfOrNull { it.items.size } ?: 0

                                    _state.update { s ->
                                        val currentStatus = s.activeSearchStatus
                                        val remaining = when (currentStatus) {
                                            is ActiveSearchStatus.Searching -> currentStatus.remainingTimeSeconds
                                            is ActiveSearchStatus.FirstOfferArrived -> currentStatus.remainingTimeSeconds
                                            is ActiveSearchStatus.MultipleOffersArrived -> currentStatus.remainingTimeSeconds
                                            else -> 900
                                        }
                                        if (offers.size == 1) {
                                            s.copy(
                                                activeSearchStatus = ActiveSearchStatus.FirstOfferArrived(
                                                    requestId = request.id,
                                                    remainingTimeSeconds = remaining,
                                                    minPrice = minPrice,
                                                    foundCount = maxFoundCount,
                                                    totalCount = totalCount
                                                )
                                            )
                                        } else {
                                            s.copy(
                                                activeSearchStatus = ActiveSearchStatus.MultipleOffersArrived(
                                                    requestId = request.id,
                                                    remainingTimeSeconds = remaining,
                                                    minPrice = minPrice,
                                                    totalOffers = offers.size,
                                                    foundCount = maxFoundCount,
                                                    totalCount = totalCount
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
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

            HomeUIIntent.OnStartSearchSimulation -> { /* Deprecated */ }
            HomeUIIntent.OnCancelSearchSimulation -> {
                viewModelScope.launch {
                    clearActiveRequestUseCase()
                }
            }
            HomeUIIntent.OnViewOffersClick -> {
                sendEffect(HomeUIEffect.NavigateToOffers)
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
