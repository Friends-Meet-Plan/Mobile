package friends.mobile.feature.main.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.main.domain.model.AvailabilityResult
import friends.mobile.feature.main.domain.repository.MainRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : BaseViewModel<MainViewState, MainAction, MainViewAction>(
    initState = MainViewState.Loading,
),
    KoinComponent {

    private val mainRepository: MainRepository by inject()
    private var isLoadingInProgress = false

    init {
        viewModelScope.launch {
            loadEvents()
        }
    }

    override fun obtainEvent(event: MainViewAction) {
        when (event) {
            is MainViewAction.OnRefresh -> onRefresh()
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            if (isLoadingInProgress) return@launch

            isLoadingInProgress = true

            try {
                when (val result = mainRepository.getActiveAndPendingEvents()) {
                    is ResultWrapper.Success -> {
                        val (activeEvents, pendingEvents) = result.data
                        viewState = MainViewState.Content(
                            activeEvents = activeEvents,
                            pendingEvents = pendingEvents,
                            isRefreshing = false,
                        )
                    }
                    is ResultWrapper.Error -> {
                        val currentState = viewState
                        val userError = mapApiErrorToUserFriendly(result.error)

                        if (currentState is MainViewState.Content) {
                            viewState = currentState.copy(isRefreshing = false)
                        } else {
                            viewState = MainViewState.Error(
                                message = getErrorMessage(userError),
                            )
                        }
                    }
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

        loadEvents()
    }

    suspend fun checkAvailability(date: String): AvailabilityResult? {
        return when (val result = mainRepository.checkUserAvailability(date)) {
            is ResultWrapper.Success -> result.data
            is ResultWrapper.Error -> null
        }
    }
}
