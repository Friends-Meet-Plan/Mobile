package friends.mobile.feature.archive.presentation

import friends.mobile.feature.events.domain.model.Event

sealed class ArchiveViewState {
    data object Loading : ArchiveViewState()

    data class Error(val message: String) : ArchiveViewState()

    data class Content(
        val archivedEvents: List<Event> = emptyList(),
        val isRefreshing: Boolean = false,
    ) : ArchiveViewState()
}
