package friends.mobile.feature.main.presentation

import friends.mobile.feature.events.domain.model.Event

sealed class MainViewState {
    data object Loading : MainViewState()

    data class Error(val message: String) : MainViewState()

    data class Content(
        val activeEvents: List<Event> = emptyList(),
        val pendingEvents: List<Event> = emptyList(),
        val isRefreshing: Boolean = false,
    ) : MainViewState()
}
