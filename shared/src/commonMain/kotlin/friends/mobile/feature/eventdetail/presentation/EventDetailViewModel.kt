package friends.mobile.feature.eventdetail.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.eventdetail.domain.repository.EventDetailRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EventDetailViewModel(
    private val eventId: String,
) : BaseViewModel<EventDetailViewState, EventDetailAction, EventDetailEvent>(
    initState = EventDetailViewState.Loading,
),
    KoinComponent {

    private val eventDetailRepository: EventDetailRepository by inject()

    init {
        viewModelScope.launch {
            loadEventDetail()
        }
    }

    override fun obtainEvent(event: EventDetailEvent) {
        // Event handling removed - navigation handled natively on iOS/Android
    }

    private fun loadEventDetail() {
        viewModelScope.launch {
            when (val result = eventDetailRepository.getEventDetail(eventId)) {
                is ResultWrapper.Success -> {
                    viewState = EventDetailViewState.Content(
                        eventDetail = result.data,
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
