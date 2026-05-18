package friends.mobile.feature.events.presentation.pendingevents

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.AcceptEventUseCase
import friends.mobile.feature.events.domain.usecase.DeclineEventUseCase
import friends.mobile.feature.events.domain.usecase.GetPendingEventsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PendingEventViewModel : BaseViewModel<PendingViewState, PendingAction, PendingEvent>(
    initState = PendingViewState.Loading,
), KoinComponent {

    private val getPendingEventsUseCase: GetPendingEventsUseCase by inject()
    private val acceptEventUseCase: AcceptEventUseCase by inject()
    private val declineEventUseCase: DeclineEventUseCase by inject()

    init {
        obtainEvent(PendingEvent.OnLoad)
    }

    override fun obtainEvent(event: PendingEvent) {
        when (event) {
            is PendingEvent.OnLoad -> loadPendingEvents(showLoading = true)
            is PendingEvent.OnRefresh -> loadPendingEvents(showLoading = false)
            is PendingEvent.OnEventClick -> onEventClick(event.eventId)
            is PendingEvent.OnAcceptEvent -> acceptEvent(event.eventId)
            is PendingEvent.OnDeclineEvent -> declineEvent(event.eventId)
        }
    }

    private fun loadPendingEvents(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) {
                viewState = PendingViewState.Loading
            } else {
                (viewState as? PendingViewState.Content)?.let { content ->
                    viewState = content.copy(isRefreshing = true)
                } ?: run {
                    viewState = PendingViewState.Loading
                }
            }

            when (val result = getPendingEventsUseCase()) {
                is ResultWrapper.Success -> {
                    viewState = PendingViewState.Content(events = result.data)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = PendingViewState.Error(message = getErrorMessage(userError))
                }
            }
        }
    }

    private fun onEventClick(eventId: String) {
        viewAction = PendingAction.NavigateToEventDetails(eventId)
    }

    private fun acceptEvent(eventId: String) {
        viewModelScope.launch {
            when (val result = acceptEventUseCase(eventId)) {
                is ResultWrapper.Success -> {
                    loadPendingEvents(showLoading = false)
                    viewAction = PendingAction.ShowMessage("Приглашение принято")
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    val errorMessage = getErrorMessage(userError)
                    viewAction = PendingAction.ShowMessage(errorMessage)
                }
            }
        }
    }

    private fun declineEvent(eventId: String) {
        viewModelScope.launch {
            when (val result = declineEventUseCase(eventId)) {
                is ResultWrapper.Success -> {
                    loadPendingEvents(showLoading = false)
                    viewAction = PendingAction.ShowMessage("Приглашение отклонено")
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    val errorMessage = getErrorMessage(userError)
                    viewAction = PendingAction.ShowMessage(errorMessage)
                }
            }
        }
    }
}
