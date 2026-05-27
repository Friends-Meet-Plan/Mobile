package friends.mobile.feature.events.presentation.eventdetail

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.GetEventDetailUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class EventDetailViewModel(
    private val eventId: String,
) : BaseViewModel<EventDetailViewState, EventDetailAction, EventDetailEvent>(
    initState = EventDetailViewState.Loading,
    screenName = AnalyticsEvent.LAUNCH_EVENT_DETAIL,
) {

    private val getEventDetailUseCase: GetEventDetailUseCase by inject()

    init {
        loadEventDetail()
    }

    override fun obtainEvent(event: EventDetailEvent) {
        when (event) {
            is EventDetailEvent.OnRefresh -> loadEventDetail()
        }
    }

    private fun loadEventDetail() {
        viewModelScope.launch {
            viewState = EventDetailViewState.Loading
            when (val result = getEventDetailUseCase(eventId)) {
                is ResultWrapper.Success -> {
                    viewState = EventDetailViewState.Content(
                        event = result.data,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = EventDetailViewState.Error(
                        message = getErrorMessage(userError),
                    )
                }
            }
        }
    }
}
