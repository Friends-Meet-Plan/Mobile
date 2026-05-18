package friends.mobile.feature.events.presentation.pendingevents

import friends.mobile.feature.eventdetail.domain.model.EventDetail
import friends.mobile.feature.events.domain.model.Event

sealed class PendingViewState {
    data object Loading : PendingViewState()
    data class Error(val message: String) : PendingViewState()
    data class Content(
        val events: List<Event>,
        val isRefreshing: Boolean = false,
        val selectedEventDetail: EventDetail? = null,
        val isLoadingDetail: Boolean = false,
        val detailError: String? = null,
    ) : PendingViewState()
}
