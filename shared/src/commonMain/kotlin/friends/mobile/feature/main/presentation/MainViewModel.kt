package friends.mobile.feature.main.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.main.domain.repository.MainRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : BaseViewModel<MainViewState, MainAction, MainViewAction>(
    initState = MainViewState.Loading,
),
    KoinComponent {

    private val mainRepository: MainRepository by inject()

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
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = MainViewState.Error(
                        message = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun onRefresh() {
        val currentState = viewState
        if (currentState is MainViewState.Content) {
            viewState = currentState.copy(isRefreshing = true)
        }
        loadEvents()
    }
}
