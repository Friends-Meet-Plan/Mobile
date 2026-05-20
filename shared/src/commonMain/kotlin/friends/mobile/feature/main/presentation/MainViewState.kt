package friends.mobile.feature.main.presentation

import friends.mobile.feature.main.domain.model.MainEvent

sealed class MainViewState {
    data object Loading : MainViewState()

    data class Error(val message: String) : MainViewState()

    data class Content(
        val activeEvents: List<MainEvent> = emptyList(),
        val pendingEvents: List<MainEvent> = emptyList(),
        val isRefreshing: Boolean = false,
    ) : MainViewState()
}
