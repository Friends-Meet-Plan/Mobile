package friends.mobile.feature.main.presentation

import friends.mobile.core.domain.model.ApiError
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.CheckUserAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.GetAcceptedEventsUseCase
import friends.mobile.feature.events.domain.usecase.GetPendingEventsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class MainViewModel : BaseViewModel<
        MainViewState,
        MainAction,
        MainViewAction,
        >(
    initState = MainViewState.Loading,
    screenName = AnalyticsEvent.LAUNCH_HOME,
) {

    private val getAcceptedEventsUseCase: GetAcceptedEventsUseCase by inject()

    private val getPendingEventsUseCase: GetPendingEventsUseCase by inject()

    private val checkUserAvailabilityUseCase: CheckUserAvailabilityUseCase by inject()

    private var isLoadingInProgress = false

    init {
        loadEvents()
    }

    override fun obtainEvent(event: MainViewAction) {
        when (event) {
            MainViewAction.OnRefresh -> onRefresh()
        }
    }

    private fun loadEvents(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (isLoadingInProgress) return@launch

            isLoadingInProgress = true

            try {
                if (showLoading) {
                    viewState = MainViewState.Loading
                } else {
                    val currentState = viewState
                    if (currentState is MainViewState.Content) {
                        viewState = currentState.copy(isRefreshing = true)
                    }
                }

                when (val activeResult = getAcceptedEventsUseCase()) {

                    is ResultWrapper.Success -> {

                        when (val pendingResult = getPendingEventsUseCase()) {

                            is ResultWrapper.Success -> {
                                viewState = MainViewState.Content(
                                    activeEvents = activeResult.data,
                                    pendingEvents = pendingResult.data,
                                    isRefreshing = false,
                                )
                            }

                            is ResultWrapper.Error -> handleError(pendingResult.error)
                        }
                    }

                    is ResultWrapper.Error -> handleError(activeResult.error)
                }
            } finally {
                isLoadingInProgress = false
            }
        }
    }

    private fun onRefresh() {
        if (isLoadingInProgress) return

        val currentState = viewState
        if (currentState is MainViewState.Content) {
            viewState = currentState.copy(isRefreshing = true)
        } else if (currentState is MainViewState.Error) {
            viewState = MainViewState.Content(isRefreshing = true)
        }

        loadEvents(showLoading = false)
    }

    suspend fun checkAvailability(
        date: String,
    ): Boolean? {

        return when (
            val result = checkUserAvailabilityUseCase(date)
        ) {

            is ResultWrapper.Success -> {
                result.data
            }

            is ResultWrapper.Error -> {
                null
            }
        }
    }

    private fun handleError(error: ApiError) {
        logError(RuntimeException("MainViewModel: $error"))

        val userError = mapApiErrorToUserFriendly(error)
        val currentState = viewState

        if (currentState is MainViewState.Content) {
            viewState = currentState.copy(isRefreshing = false)
        } else {
            viewState = MainViewState.Error(message = getErrorMessage(userError))
        }
    }

    private inline fun updateContent(
        transform: MainViewState.Content.() -> MainViewState.Content,
    ) {
        val currentState =
            viewState as? MainViewState.Content ?: return

        viewState = currentState.transform()
    }
}