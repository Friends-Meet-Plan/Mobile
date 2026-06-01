package friends.mobile.feature.events.presentation.createevent

sealed class CreateEventAction {
    data class NavigateToEventDetail(val eventId: String) : CreateEventAction()
    data object NavigateBack : CreateEventAction()
}
