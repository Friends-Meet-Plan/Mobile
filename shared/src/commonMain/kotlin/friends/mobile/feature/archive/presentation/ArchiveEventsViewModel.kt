package friends.mobile.feature.archive.presentation

import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.GetArchivedEventsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ArchiveEventsViewModel : BaseViewModel<ArchiveViewState, Unit, ArchiveViewAction>(
    initState = ArchiveViewState.Loading,
    screenName = AnalyticsEvent.LAUNCH_ARCHIVE,
) {

    private val getArchivedEventsUseCase: GetArchivedEventsUseCase by inject()

    init {
        loadArchivedEvents()
    }

    override fun obtainEvent(event: ArchiveViewAction) {
        when (event) {
            is ArchiveViewAction.OnRefresh -> onRefresh()
        }
    }

    private fun loadArchivedEvents() {
        viewModelScope.launch {
            when (val result = getArchivedEventsUseCase()) {
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
        updateContent {
            it.copy(isRefreshing = true)
        }
        loadArchivedEvents()
    }

    private fun updateContent(
        transform: (ArchiveViewState.Content) -> ArchiveViewState.Content,
    ) {
        val currentState = viewState as? ArchiveViewState.Content ?: return
        viewState = transform(currentState)
    }
}
