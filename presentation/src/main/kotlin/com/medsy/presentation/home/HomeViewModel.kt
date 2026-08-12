package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.categories.usecase.GetCategoriesUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.profile.usecase.GetProfileUseCase
import com.medsy.domain.requests.usecase.GetActiveRequestsUseCase
import com.medsy.domain.requests.usecase.ObserveActiveRequestsWithStatusUseCase
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.home.HomeUIEffect.NavigateToCategory
import com.medsy.presentation.home.HomeUIEffect.NavigateToOffers
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.usecase.DetermineOrderNextActionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val getActiveRequestsUseCase: GetActiveRequestsUseCase,
    private val observeActiveRequestsWithStatusUseCase: ObserveActiveRequestsWithStatusUseCase,
    private val determineOrderNextActionUseCase: DetermineOrderNextActionUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    private val _effect = Channel<HomeUIEffect>()
    val effect = _effect.receiveAsFlow()

    var language: String = Locale.getDefault().language

    init {
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

    private var activeRequestsJob: Job? = null

    private fun refreshActiveRequests() {
        activeRequestsJob?.cancel()
        activeRequestsJob = viewModelScope.launch {
            getActiveRequestsUseCase()
                .onSuccess { lookup ->
                    _state.update { it.copy(resumableOrder = lookup.resumableOrder) }
                    observeActiveRequestsWithStatusUseCase(lookup.activeRequests).collectLatest { statuses ->
                        _state.update { it.copy(activeSearchStatuses = statuses) }
                    }
                }
                .onError {
                    _state.update {
                        it.copy(activeSearchStatuses = emptyList(), resumableOrder = null)
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

            is HomeUIIntent.OnViewOffersClick -> {
                sendEffect(NavigateToOffers(intent.requestId))
            }

            HomeUIIntent.OnContinueOrderClick -> {
                _state.value.resumableOrder?.let { order ->
                    when (determineOrderNextActionUseCase(order)) {
                        OrderNextAction.CHOOSE_FULFILLMENT,
                        OrderNextAction.PAY_CARD -> sendEffect(
                            HomeUIEffect.NavigateToOrderReview(order.requestId, order.id)
                        )
                        OrderNextAction.VIEW_DETAILS -> Unit
                    }
                }
            }

            HomeUIIntent.OnResume -> refreshActiveRequests()

            HomeUIIntent.RefreshData -> {
                loadHome()
                refreshActiveRequests()
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
