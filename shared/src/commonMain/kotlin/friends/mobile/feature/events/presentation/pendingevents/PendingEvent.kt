package friends.mobile.feature.events.presentation.pendingevents

sealed class PendingEvent {
    data object OnLoad : PendingEvent()
    data object OnRefresh : PendingEvent()
    data class OnEventClick(val eventId: String) : PendingEvent()
    data class OnAcceptEvent(val eventId: String) : PendingEvent()
    data class OnDeclineEvent(val eventId: String) : PendingEvent()
    data object OnDismissDetail : PendingEvent()
    data object OnBackClick : PendingEvent()
}
