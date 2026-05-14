package friends.mobile.feature.events.presentation

import friends.mobile.feature.friends.domain.model.User

sealed class CreateEventViewState {
    data object Loading : CreateEventViewState()

    data class Error(val message: String) : CreateEventViewState()

    data class Content(
        val selectedDate: String,
        val title: String = "",
        val description: String = "",
        val location: String = "",
        val time: String = "",
        val availableFriends: List<User> = emptyList(),
        val selectedFriendIds: Set<String> = emptySet(),
        val isLoadingFriends: Boolean = false,
        val friendsError: String? = null,
        val isCreatingEvent: Boolean = false,
        val isCreateButtonEnabled: Boolean = false,
    ) : CreateEventViewState()
}
