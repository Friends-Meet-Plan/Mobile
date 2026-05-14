package friends.mobile.feature.eventdetail.presentation

import friends.mobile.feature.eventdetail.domain.model.EventDetail

sealed class EventDetailViewState {
    data object Loading : EventDetailViewState()

    data class Error(val message: String) : EventDetailViewState()

    data class Content(val eventDetail: EventDetail) : EventDetailViewState()
}
