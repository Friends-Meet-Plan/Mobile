package friends.mobile.feature.event.presentation.create

import friends.mobile.feature.friends.domain.model.User

/**
 * MVI view state for the create event screen.
 *
 * States:
 *   - Loading: initial state while fetching data (e.g., available friends)
 *   - Error: something went wrong during initial load
 *   - Content: successfully loaded and ready for user input
 *
 * Properties:
 *   - selectedDate: the pre-selected date injected by the previous screen (immutable)
 *   - dateError: validation error for the date field
 *   - titleInput: current value of the title input field
 *   - titleError: validation error for the title field
 *   - descriptionInput: current value of the description field
 *   - locationInput: current value of the location field
 *   - availableFriends: list of friends who are free on the selected date
 *   - isLoadingFriends: true while fetching available friends
 *   - friendsError: error message if fetching friends failed
 *   - selectedFriendIds: set of friend IDs currently selected to invite
 *   - isCreatingEvent: true while the create event API call is in progress
 *   - creationError: error message if event creation failed
 */
sealed class CreateEventViewState {
    data object Loading : CreateEventViewState()

    data class Error(val message: String) : CreateEventViewState()

    data class Content(
        val selectedDate: String = "",
        val dateError: String? = null,

        val titleInput: String = "",
        val titleError: String? = null,

        val descriptionInput: String = "",
        val locationInput: String = "",

        val availableFriends: List<User> = emptyList(),
        val isLoadingFriends: Boolean = false,
        val friendsError: String? = null,

        val selectedFriendIds: Set<String> = emptySet(),

        val isCreatingEvent: Boolean = false,
        val creationError: String? = null,
    ) : CreateEventViewState()
}
