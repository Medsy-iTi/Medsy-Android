package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.requests.model.ActiveRequestStatus
import com.medsy.domain.requests.usecase.ObserveActiveRequestsWithStatusUseCase
import com.medsy.domain.requests.usecase.RemoveActiveRequestUseCase
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.home.HomeUIEffect.NavigateToCategory
import com.medsy.presentation.home.HomeUIEffect.NavigateToOffers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

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

    var language: String = Locale.getDefault().language

    init {
        observeActiveRequest()
        loadHome()
    }

    private fun loadHome() {
        getProfileData()
        fetchCategories()
    }

    private fun getProfileData() {
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
            getCategoriesUseCase(page = 0, size = 9).collectLatest { result ->
                result.onSuccess { domainCategories ->
                    val uiCategories = domainCategories.map {
                        CategoryUi(
                            id = it.id.toString(),
                            name = it.name,
                            imageRes = it.image
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
                sendEffect(NavigateToCategory(intent.categoryId, categoryName))
            }

            is HomeUIIntent.OnCancelSearchSimulation -> {
                viewModelScope.launch {
                    removeActiveRequestUseCase(intent.requestId)
                }
            }

            is HomeUIIntent.OnViewOffersClick -> {
                sendEffect(NavigateToOffers(intent.requestId))
            }

            HomeUIIntent.OnSearchWiderRangeClick -> { /* Refresh/Widen Search */
            }

            HomeUIIntent.RefreshData -> {
                loadHome()
            }

            is HomeUIIntent.LanguageChanged -> {
                if (language != intent.languageTag) {
                    language = intent.languageTag
                    loadHome()
                }
            }
        }
    }


    private fun sendEffect(effect: HomeUIEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
