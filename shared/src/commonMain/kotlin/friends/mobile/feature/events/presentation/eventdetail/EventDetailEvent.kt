package friends.mobile.feature.events.presentation.eventdetail

sealed class EventDetailEvent {
    data object OnRefresh : EventDetailEvent()
}
