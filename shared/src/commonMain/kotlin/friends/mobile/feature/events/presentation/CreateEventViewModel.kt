package friends.mobile.feature.events.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.auth.domain.usecase.GetStoredSessionUseCase
import friends.mobile.feature.events.domain.usecase.CheckFriendsAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.CreateEventUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateEventViewModel(
    private val selectedDate: String,
) : BaseViewModel<CreateEventViewState, CreateEventAction, CreateEventEvent>(
    initState = CreateEventViewState.Loading,
),
    KoinComponent {

    private val checkFriendsAvailabilityUseCase: CheckFriendsAvailabilityUseCase by inject()
    private val createEventUseCase: CreateEventUseCase by inject()
    private val getStoredSessionUseCase: GetStoredSessionUseCase by inject()

    init {
        viewModelScope.launch {
            loadAvailableFriends()
        }
    }

    override fun obtainEvent(event: CreateEventEvent) {
        when (event) {
            is CreateEventEvent.OnTitleChanged -> updateContent { it.copy(title = event.title, isCreateButtonEnabled = computeIsCreateButtonEnabled(event.title, it.selectedFriendIds)) }
            is CreateEventEvent.OnDescriptionChanged -> updateContent { it.copy(description = event.description) }
            is CreateEventEvent.OnLocationChanged -> updateContent { it.copy(location = event.location) }
            is CreateEventEvent.OnToggleFriend -> onToggleFriend(event.friendId)
            is CreateEventEvent.OnSelectFriendsSheet -> {}
            is CreateEventEvent.OnCreateEvent -> onCreateEvent()
        }
    }

    private fun loadAvailableFriends() {
        viewModelScope.launch {
            when (val result = checkFriendsAvailabilityUseCase(selectedDate)) {
                is ResultWrapper.Success -> {
                    // Check if the owner (current user) is available on this date
                    val currentSession = getStoredSessionUseCase()
                    val ownerId = currentSession?.user?.id

                    // The API returns only available friends, so owner is available if:
                    // 1. Owner ID exists in the available friends list, OR
                    // 2. If owner is not in the list, it means they're busy
                    val availableFriendsIds = result.data.map { it.id }.toSet()
                    val isOwnerAvailable = ownerId != null && availableFriendsIds.contains(ownerId)

                    viewState = CreateEventViewState.Content(
                        selectedDate = selectedDate,
                        availableFriends = result.data,
                        isLoadingFriends = false,
                        isOwnerAvailable = isOwnerAvailable,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = CreateEventViewState.Error(
                        message = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun onToggleFriend(friendId: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        val newSelectedIds = currentState.selectedFriendIds.toMutableSet()
        if (newSelectedIds.contains(friendId)) {
            newSelectedIds.remove(friendId)
        } else {
            newSelectedIds.add(friendId)
        }
        val newSelectedSet = newSelectedIds.toSet()
        viewState = currentState.copy(
            selectedFriendIds = newSelectedSet,
            isCreateButtonEnabled = computeIsCreateButtonEnabled(currentState.title, newSelectedSet),
        )
    }

    private fun onCreateEvent() {
        val currentState = viewState as? CreateEventViewState.Content ?: return

        if (currentState.title.isBlank()) {
            viewState = CreateEventViewState.Error(message = "Event title is required")
            return
        }

        if (currentState.selectedFriendIds.isEmpty()) {
            viewState = CreateEventViewState.Error(message = "Please select at least one friend")
            return
        }

        if (!currentState.isOwnerAvailable) {
            viewState = CreateEventViewState.Error(message = "You are not available on this date. Please select a different date for the event.")
            return
        }

        val invitedIds = currentState.selectedFriendIds.toList()

        viewState = currentState.copy(isCreatingEvent = true)

        viewModelScope.launch {
            when (val result = createEventUseCase(
                title = currentState.title,
                description = currentState.description.takeIf { it.isNotBlank() },
                date = currentState.selectedDate,
                time = currentState.time.takeIf { it.isNotBlank() },
                location = currentState.location.takeIf { it.isNotBlank() },
                invitedFriendIds = invitedIds,
            )) {
                is ResultWrapper.Success -> {
                    viewAction = CreateEventAction.NavigateToEventDetail(eventId = result.data)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = CreateEventViewState.Error(
                        message = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun onBack() {
        viewAction = CreateEventAction.NavigateBack
    }

    private fun updateContent(transform: (CreateEventViewState.Content) -> CreateEventViewState.Content) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        viewState = transform(currentState)
    }

    private fun computeIsCreateButtonEnabled(title: String, selectedFriendIds: Set<String>): Boolean {
        val currentState = viewState as? CreateEventViewState.Content ?: return false
        return title.isNotBlank() && selectedFriendIds.isNotEmpty() && currentState.isOwnerAvailable
    }
}
