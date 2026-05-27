package friends.mobile.feature.events.presentation

import friends.mobile.core.domain.model.ApiError
import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.CheckFriendsAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.CheckUserAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.CreateEventUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateEventViewModel(
    private val selectedDate: String,
) : BaseViewModel<
        CreateEventViewState,
        CreateEventAction,
        CreateEventEvent,
        >(
    initState = CreateEventViewState.Loading,
), KoinComponent {

    private val checkFriendsAvailabilityUseCase: CheckFriendsAvailabilityUseCase by inject()
    private val checkUserAvailabilityUseCase: CheckUserAvailabilityUseCase by inject()
    private val createEventUseCase: CreateEventUseCase by inject()

    init {
        viewModelScope.launch {
            loadAvailableFriends()
        }
    }

    override fun obtainEvent(event: CreateEventEvent) {
        when (event) {

            is CreateEventEvent.OnTitleChanged -> {
                updateContent {
                    it.copy(
                        title = event.title,
                        isCreateButtonEnabled = computeCreateButtonEnabled(
                            title = event.title,
                            selectedFriendIds = it.selectedFriendIds,
                            isOwnerAvailable = it.isOwnerAvailable,
                        ),
                    )
                }
            }

            is CreateEventEvent.OnDescriptionChanged -> {
                updateContent {
                    it.copy(description = event.description)
                }
            }

            is CreateEventEvent.OnLocationChanged -> {
                updateContent {
                    it.copy(location = event.location)
                }
            }

            is CreateEventEvent.OnToggleFriend -> {
                toggleFriend(event.friendId)
            }

            CreateEventEvent.OnCreateEvent -> {
                createEvent()
            }
        }
    }

    private suspend fun loadAvailableFriends() {

        when (val friendsResult = checkFriendsAvailabilityUseCase(selectedDate)) {

            is ResultWrapper.Success -> {

                when (val availabilityResult = checkUserAvailabilityUseCase(selectedDate)) {

                    is ResultWrapper.Success -> {
                        viewState = CreateEventViewState.Content(
                            selectedDate = selectedDate,
                            availableFriends = friendsResult.data,
                            isOwnerAvailable = availabilityResult.data,
                            isLoadingFriends = false,
                        )
                    }

                    is ResultWrapper.Error -> {
                        handleError(availabilityResult.error)
                    }
                }
            }

            is ResultWrapper.Error -> {
                handleError(friendsResult.error)
            }
        }
    }

    private fun toggleFriend(friendId: String) {

        updateContent { currentState ->

            val updatedIds = currentState.selectedFriendIds.toMutableSet()

            if (friendId in updatedIds) {
                updatedIds.remove(friendId)
            } else {
                updatedIds.add(friendId)
            }

            val selectedIds = updatedIds.toSet()

            currentState.copy(
                selectedFriendIds = selectedIds,
                isCreateButtonEnabled = computeCreateButtonEnabled(
                    title = currentState.title,
                    selectedFriendIds = selectedIds,
                    isOwnerAvailable = currentState.isOwnerAvailable,
                ),
            )
        }
    }

    private fun createEvent() {

        val currentState = viewState as? CreateEventViewState.Content
            ?: return

        when {
            currentState.title.isBlank() -> {
                viewState = CreateEventViewState.Error(
                    message = "Event title is required",
                )
                return
            }

            currentState.selectedFriendIds.isEmpty() -> {
                viewState = CreateEventViewState.Error(
                    message = "Please select at least one friend",
                )
                return
            }

            !currentState.isOwnerAvailable -> {
                viewState = CreateEventViewState.Error(
                    message = "You are not available on this date. Please select a different date for the event.",
                )
                return
            }
        }

        updateContent {
            it.copy(isCreatingEvent = true)
        }

        viewModelScope.launch {

            when (
                val result = createEventUseCase(
                    title = currentState.title,
                    description = currentState.description.takeIf { it.isNotBlank() },
                    date = currentState.selectedDate,
                    time = currentState.time.takeIf { it.isNotBlank() },
                    location = currentState.location.takeIf { it.isNotBlank() },
                    invitedFriendIds = currentState.selectedFriendIds.toList(),
                )
            ) {

                is ResultWrapper.Success -> {
                    viewAction = CreateEventAction.NavigateToEventDetail(
                        eventId = result.data,
                    )
                }

                is ResultWrapper.Error -> {
                    handleError(result.error)
                }
            }
        }
    }

    private fun handleError(error: ApiError) {
        val userError = mapApiErrorToUserFriendly(error)

        viewState = CreateEventViewState.Error(
            message = getErrorMessage(userError),
        )
    }

    private inline fun updateContent(
        transform: (CreateEventViewState.Content) -> CreateEventViewState.Content,
    ) {
        val currentState = viewState as? CreateEventViewState.Content
            ?: return

        viewState = transform(currentState)
    }

    private fun computeCreateButtonEnabled(
        title: String,
        selectedFriendIds: Set<String>,
        isOwnerAvailable: Boolean,
    ): Boolean {
        return title.isNotBlank() &&
                selectedFriendIds.isNotEmpty() &&
                isOwnerAvailable
    }
}