package friends.mobile.feature.archive.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.main.domain.repository.MainRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ArchiveEventsViewModel : BaseViewModel<ArchiveViewState, Unit, ArchiveViewAction>(
    initState = ArchiveViewState.Loading,
),
    KoinComponent {

    private val mainRepository: MainRepository by inject()

    init {
        viewModelScope.launch {
            loadArchivedEvents()
        }
    }

    override fun obtainEvent(event: ArchiveViewAction) {
        when (event) {
            is ArchiveViewAction.OnRefresh -> onRefresh()
        }
    }

    private fun loadArchivedEvents() {
        viewModelScope.launch {
            when (val result = mainRepository.getArchivedEvents()) {
                is ResultWrapper.Success -> {
                    viewState = ArchiveViewState.Content(
                        archivedEvents = result.data,
                        isRefreshing = false,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = ArchiveViewState.Error(
                        message = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun onRefresh() {
        val currentState = viewState
        if (currentState is ArchiveViewState.Content) {
            viewState = currentState.copy(isRefreshing = true)
        }
        loadArchivedEvents()
    }
}
