package friends.mobile.feature.archive.presentation

sealed class ArchiveViewAction {
    data object OnRefresh : ArchiveViewAction()
}
