package friends.mobile.feature.events.presentation.pendingevents

sealed class PendingAction {
    data class ShowMessage(val message: String) : PendingAction()
    data object NavigateBack : PendingAction()
}
