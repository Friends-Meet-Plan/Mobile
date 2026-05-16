package friends.mobile.feature.events.presentation

sealed class CreateEventAction {
    data class NavigateToEventDetail(val eventId: String) : CreateEventAction()
    data object NavigateBack : CreateEventAction()
}
