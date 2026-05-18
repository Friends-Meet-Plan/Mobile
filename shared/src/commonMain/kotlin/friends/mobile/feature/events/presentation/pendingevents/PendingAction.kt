package friends.mobile.feature.events.presentation.pendingevents

import friends.mobile.feature.events.domain.model.Event

sealed class PendingAction {
    data class NavigateToEventDetails(val eventId: String) : PendingAction()
    data class ShowMessage(val message: String) : PendingAction()
    data object NavigateBack : PendingAction()
}
