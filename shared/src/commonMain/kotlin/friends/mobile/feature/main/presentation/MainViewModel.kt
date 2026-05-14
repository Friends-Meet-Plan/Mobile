package friends.mobile.feature.main.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.main.domain.repository.MainRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : BaseViewModel<MainViewState, MainAction, MainEvent>(
    initState = MainViewState.Loading,
),
    KoinComponent {

    private val mainRepository: MainRepository by inject()

    init {
        viewModelScope.launch {
            loadEvents()
        }
    }

    override fun obtainEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnRefresh -> onRefresh()
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            when (val result = mainRepository.getAcceptedEvents()) {
                is ResultWrapper.Success -> {
                    viewState = MainViewState.Content(
                        upcomingEvents = result.data,
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
