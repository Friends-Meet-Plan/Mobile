package friends.mobile.feature.event.presentation.create

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.event.domain.model.CreateEventRequest
import friends.mobile.feature.event.domain.usecase.CreateEventUseCase
import friends.mobile.feature.event.domain.usecase.GetAvailableFriendsUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * ViewModel for the create event screen.
 *
 * Manages form state, validation, and orchestrates use cases.
 * Accepts a pre-selected date (injected from the previous screen).
 *
 * @param selectedDate the date in "YYYY-MM-DD" format, or null if not pre-selected
 */
class CreateEventViewModel(
    private val selectedDate: String? = null,
) : BaseViewModel<CreateEventViewState, CreateEventAction, CreateEventEvent>(
    initState = CreateEventViewState.Content(
        selectedDate = selectedDate ?: "",
    ),
), KoinComponent {

    private val createEventUseCase: CreateEventUseCase by inject()
    private val getAvailableFriendsUseCase: GetAvailableFriendsUseCase by inject()

    override fun obtainEvent(event: CreateEventEvent) {
        when (event) {
            is CreateEventEvent.ScreenOpened -> onScreenOpened()
            is CreateEventEvent.OnTitleChanged -> onTitleChanged(event.title)
            is CreateEventEvent.OnDescriptionChanged -> onDescriptionChanged(event.description)
            is CreateEventEvent.OnLocationChanged -> onLocationChanged(event.location)
            is CreateEventEvent.OnDateChanged -> onDateChanged(event.date)
            is CreateEventEvent.FetchAvailableFriends -> fetchAvailableFriends()
            is CreateEventEvent.OnFriendSelected -> onFriendSelected(event.friendId)
            is CreateEventEvent.OnFriendDeselected -> onFriendDeselected(event.friendId)
            is CreateEventEvent.OnCreateEventClicked -> onCreateEventClicked()
            is CreateEventEvent.OnCancelClicked -> onCancelClicked()
        }
    }

    private fun onScreenOpened() {
        val currentState = viewState as? CreateEventViewState.Content ?: return

        // Validate that a date was injected
        if (currentState.selectedDate.isBlank()) {
            viewState = CreateEventViewState.Error("No date selected for event creation")
            return
        }

        // Fetch available friends for the selected date
        fetchAvailableFriends()
    }

    private fun onTitleChanged(title: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        viewState = currentState.copy(titleInput = title, titleError = null)
    }

    private fun onDescriptionChanged(description: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        viewState = currentState.copy(descriptionInput = description)
    }

    private fun onLocationChanged(location: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        viewState = currentState.copy(locationInput = location)
    }

    private fun onDateChanged(date: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        viewState = currentState.copy(
            selectedDate = date,
            dateError = null,
        )
    }

    private fun fetchAvailableFriends() {
        val currentState = viewState as? CreateEventViewState.Content ?: return

        if (currentState.selectedDate.isBlank()) {
            viewState = currentState.copy(dateError = "Date is required")
            return
        }

        viewModelScope.launch {
            viewState = currentState.copy(isLoadingFriends = true, friendsError = null)

            when (val result = getAvailableFriendsUseCase(currentState.selectedDate)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        availableFriends = result.data,
                        isLoadingFriends = false,
                        friendsError = null,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isLoadingFriends = false,
                        friendsError = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun onFriendSelected(friendId: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        val updatedFriends = currentState.selectedFriendIds.toMutableSet().apply { add(friendId) }
        viewState = currentState.copy(selectedFriendIds = updatedFriends)
    }

    private fun onFriendDeselected(friendId: String) {
        val currentState = viewState as? CreateEventViewState.Content ?: return
        val updatedFriends = currentState.selectedFriendIds.toMutableSet().apply { remove(friendId) }
        viewState = currentState.copy(selectedFriendIds = updatedFriends)
    }

    private fun onCreateEventClicked() {
        val currentState = viewState as? CreateEventViewState.Content ?: return

        // Validate form fields
        val titleError = if (currentState.titleInput.isBlank()) {
            "Event title is required"
        } else {
            null
        }

        val dateError = if (currentState.selectedDate.isBlank()) {
            "Event date is required"
        } else {
            null
        }

        // If there are validation errors, update state and return early
        if (titleError != null || dateError != null) {
            viewState = currentState.copy(
                titleError = titleError,
                dateError = dateError,
            )
            return
        }

        // All validations passed, proceed with event creation
        viewModelScope.launch {
            viewState = currentState.copy(
                isCreatingEvent = true,
                creationError = null,
            )

            val request = CreateEventRequest(
                date = currentState.selectedDate,
                title = currentState.titleInput,
                description = currentState.descriptionInput.ifBlank { null },
                location = currentState.locationInput.ifBlank { null },
                participantIds = currentState.selectedFriendIds.toList(),
            )

            when (val result = createEventUseCase(request)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(isCreatingEvent = false)
                    viewAction = CreateEventAction.EventCreatedSuccess
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    val errorMessage = getErrorMessage(userError)
                    viewState = currentState.copy(
                        isCreatingEvent = false,
                        creationError = errorMessage,
                    )
                    viewAction = CreateEventAction.ShowError(errorMessage)
                }
            }
        }
    }

    private fun onCancelClicked() {
        viewAction = CreateEventAction.NavigateBack
    }
}
