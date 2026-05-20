package friends.mobile.feature.archive.presentation

import friends.mobile.feature.main.domain.model.MainEvent

sealed class ArchiveViewState {
    data object Loading : ArchiveViewState()

    data class Error(val message: String) : ArchiveViewState()

    data class Content(
        val archivedEvents: List<MainEvent> = emptyList(),
        val isRefreshing: Boolean = false,
    ) : ArchiveViewState()
}
