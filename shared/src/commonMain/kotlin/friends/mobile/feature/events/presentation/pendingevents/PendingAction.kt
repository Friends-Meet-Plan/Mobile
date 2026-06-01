package friends.mobile.feature.events.presentation.pendingevents

sealed class PendingAction {
    data object ShowAcceptSuccess : PendingAction()
    data object ShowDeclineSuccess : PendingAction()
    data class ShowError(val message: String) : PendingAction()
    data object NavigateBack : PendingAction()
}
