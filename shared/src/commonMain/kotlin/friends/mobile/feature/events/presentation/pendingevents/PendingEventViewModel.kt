package friends.mobile.feature.events.presentation.pendingevents

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.analytics.AnalyticsEvent
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.AcceptEventUseCase
import friends.mobile.feature.events.domain.usecase.DeclineEventUseCase
import friends.mobile.feature.events.domain.usecase.GetEventDetailUseCase
import friends.mobile.feature.events.domain.usecase.GetPendingEventsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class PendingEventViewModel : BaseViewModel<PendingViewState, PendingAction, PendingEvent>(
    initState = PendingViewState.Loading,
    screenName = AnalyticsEvent.LAUNCH_PENDING_EVENTS,
) {

    private val getPendingEventsUseCase: GetPendingEventsUseCase by inject()
    private val getEventDetailUseCase: GetEventDetailUseCase by inject()
    private val acceptEventUseCase: AcceptEventUseCase by inject()
    private val declineEventUseCase: DeclineEventUseCase by inject()

    init {
        obtainEvent(PendingEvent.OnLoad)
    }

    override fun obtainEvent(event: PendingEvent) {
        when (event) {

            PendingEvent.OnLoad -> {
                viewModelScope.launch {
                    loadPendingEvents(showLoading = true)
                }
            }

            PendingEvent.OnRefresh -> {
                viewModelScope.launch {
                    loadPendingEvents(showLoading = false)
                }
            }

            is PendingEvent.OnEventClick -> {
                fetchEventDetail(event.eventId)
            }

            is PendingEvent.OnAcceptEvent -> {
                acceptEvent(event.eventId)
            }

            is PendingEvent.OnDeclineEvent -> {
                declineEvent(event.eventId)
            }

            PendingEvent.OnDismissDetail -> {
                dismissDetail()
            }

            PendingEvent.OnBackClick -> {
                viewAction = PendingAction.NavigateBack
            }
        }
    }

    private suspend fun loadPendingEvents(showLoading: Boolean) {

        if (showLoading) {
            viewState = PendingViewState.Loading
        } else {
            updateContent {
                it.copy(isRefreshing = true)
            }
        }

        when (val result = getPendingEventsUseCase()) {

            is ResultWrapper.Success -> {
                viewState = PendingViewState.Content(
                    events = result.data,
                )
            }

            is ResultWrapper.Error -> {
                val userError = mapApiErrorToUserFriendly(result.error)

                viewState = PendingViewState.Error(
                    message = getErrorMessage(userError),
                )
            }
        }
    }

    private fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {

            updateContent {
                it.copy(
                    isLoadingDetail = true,
                    detailError = null,
                )
            }

            when (val result = getEventDetailUseCase(eventId)) {

                is ResultWrapper.Success -> {
                    updateContent {
                        it.copy(
                            selectedEventDetail = result.data,
                            isLoadingDetail = false,
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)

                    updateContent {
                        it.copy(
                            detailError = getErrorMessage(userError),
                            isLoadingDetail = false,
                        )
                    }
                }
            }
        }
    }

    private fun dismissDetail() {
        updateContent {
            it.copy(
                selectedEventDetail = null,
                detailError = null,
            )
        }
    }

    private fun acceptEvent(eventId: String) {
        viewModelScope.launch {

            when (val result = acceptEventUseCase(eventId)) {

                is ResultWrapper.Success -> {

                    dismissDetail()

                    loadPendingEvents(showLoading = false)

                    viewAction = PendingAction.ShowMessage(
                        "Приглашение принято",
                    )
                }

                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)

                    viewAction = PendingAction.ShowMessage(
                        getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun declineEvent(eventId: String) {
        viewModelScope.launch {

            when (val result = declineEventUseCase(eventId)) {

                is ResultWrapper.Success -> {

                    dismissDetail()

                    loadPendingEvents(showLoading = false)

                    viewAction = PendingAction.ShowMessage(
                        "Приглашение отклонено",
                    )
                }

                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)

                    viewAction = PendingAction.ShowMessage(
                        getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private inline fun updateContent(
        transform: (PendingViewState.Content) -> PendingViewState.Content,
    ) {
        val currentState = viewState as? PendingViewState.Content ?: return
        viewState = transform(currentState)
    }
}
