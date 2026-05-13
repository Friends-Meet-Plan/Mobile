package friends.mobile.feature.events.presentation

import friends.mobile.core.domain.model.ResultWrapper
import friends.mobile.core.domain.model.getErrorMessage
import friends.mobile.core.domain.model.mapApiErrorToUserFriendly
import friends.mobile.core.viewmodel.BaseViewModel
import friends.mobile.feature.events.domain.usecase.CheckFriendsAvailabilityUseCase
import friends.mobile.feature.events.domain.usecase.CreateEventUseCase
import friends.mobile.feature.friends.domain.model.User
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateEventViewModel(
    private val dateString: String,
) : BaseViewModel<CreateEventViewState, CreateEventAction, CreateEventEvent>(
    initState = CreateEventViewState.SelectContacts(selectedDate = dateString),
),
    KoinComponent {

    private val checkFriendsAvailabilityUseCase: CheckFriendsAvailabilityUseCase by inject()
    private val createEventUseCase: CreateEventUseCase by inject()

    init {
        viewModelScope.launch {
            loadAvailableFriends(dateString)
        }
    }

    override fun obtainEvent(event: CreateEventEvent) {
        when (event) {
            is CreateEventEvent.OnDateSelected -> onDateSelected(event.date)
            is CreateEventEvent.OnToggleFriend -> onToggleFriend(event.friendId)
            is CreateEventEvent.OnContinueToDetails -> onContinueToDetails()
            is CreateEventEvent.OnTitleChanged -> updateDetails { it.copy(title = event.title) }
            is CreateEventEvent.OnDescriptionChanged -> updateDetails { it.copy(description = event.description) }
            is CreateEventEvent.OnLocationChanged -> updateDetails { it.copy(location = event.location) }
            is CreateEventEvent.OnTimeChanged -> updateDetails { it.copy(time = event.time) }
            is CreateEventEvent.OnSubmit -> onSubmit()
            is CreateEventEvent.OnBack -> onBack()
        }
    }

    private fun onDateSelected(date: String) {
        viewState = CreateEventViewState.SelectContacts(selectedDate = date)
        viewModelScope.launch {
            loadAvailableFriends(date)
        }
    }

    private fun loadAvailableFriends(date: String) {
        val currentState = viewState as? CreateEventViewState.SelectContacts ?: return
        viewModelScope.launch {
            viewState = currentState.copy(isLoadingFriends = true, loadError = null)
            when (val result = checkFriendsAvailabilityUseCase(date)) {
                is ResultWrapper.Success -> {
                    viewState = currentState.copy(
                        availableFriends = result.data,
                        isLoadingFriends = false,
                    )
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = currentState.copy(
                        isLoadingFriends = false,
                        loadError = getErrorMessage(userError),
                    )
                }
            }
        }
    }

    private fun onToggleFriend(friendId: String) {
        val currentState = viewState as? CreateEventViewState.SelectContacts ?: return
        val newSelectedIds = currentState.selectedFriendIds.toMutableSet()
        if (newSelectedIds.contains(friendId)) {
            newSelectedIds.remove(friendId)
        } else {
            newSelectedIds.add(friendId)
        }
        viewState = currentState.copy(selectedFriendIds = newSelectedIds)
    }

    private fun onContinueToDetails() {
        val currentState = viewState as? CreateEventViewState.SelectContacts ?: return
        val selectedFriends = currentState.availableFriends.filter { user ->
            currentState.selectedFriendIds.contains(user.id)
        }
        viewState = CreateEventViewState.FillDetails(
            selectedDate = currentState.selectedDate,
            selectedFriends = selectedFriends,
        )
    }

    private fun updateDetails(transform: (CreateEventViewState.FillDetails) -> CreateEventViewState.FillDetails) {
        val currentState = viewState as? CreateEventViewState.FillDetails ?: return
        viewState = transform(currentState)
    }

    private fun onSubmit() {
        val currentState = viewState as? CreateEventViewState.FillDetails ?: return

        if (currentState.title.isBlank()) {
            viewState = CreateEventViewState.Error(
                message = "Event title is required",
                previousState = currentState,
            )
            return
        }

        val invitedIds = currentState.selectedFriends.map { it.id }

        viewState = CreateEventViewState.Submitting(
            selectedDate = currentState.selectedDate,
            selectedFriends = currentState.selectedFriends,
            title = currentState.title,
            description = currentState.description.takeIf { it.isNotBlank() },
            location = currentState.location.takeIf { it.isNotBlank() },
            time = currentState.time.takeIf { it.isNotBlank() },
        )

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
                    viewState = CreateEventViewState.Success(event = result.data)
                    viewAction = CreateEventAction.NavigateToEventDetail(eventId = result.data.id)
                }
                is ResultWrapper.Error -> {
                    val userError = mapApiErrorToUserFriendly(result.error)
                    viewState = CreateEventViewState.Error(
                        message = getErrorMessage(userError),
                        previousState = viewState,
                    )
                }
            }
        }
    }

    private fun onBack() {
        when (val current = viewState) {
            is CreateEventViewState.FillDetails -> {
                viewState = CreateEventViewState.SelectContacts(
                    selectedDate = current.selectedDate,
                    availableFriends = current.selectedFriends,
                    selectedFriendIds = current.selectedFriends.map { it.id }.toSet(),
                )
            }
            is CreateEventViewState.Error -> {
                viewState = current.previousState ?: CreateEventViewState.SelectContacts(
                    selectedDate = (current.previousState as? CreateEventViewState.FillDetails)?.selectedDate
                        ?: "",
                )
            }
            else -> viewAction = CreateEventAction.NavigateBack
        }
    }
}
