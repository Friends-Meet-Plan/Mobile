package friends.mobile.feature.events.presentation.eventdetail

import friends.mobile.feature.events.domain.model.Event

sealed class EventDetailViewState {
    data object Loading : EventDetailViewState()

    data class Error(val message: String) : EventDetailViewState()

    data class Content(val event: Event) : EventDetailViewState()
}
