package friends.mobile.feature.events.presentation

import friends.mobile.feature.events.domain.model.Event
import friends.mobile.feature.friends.domain.model.User

sealed class CreateEventViewState {
    data class SelectContacts(
        val selectedDate: String,
        val availableFriends: List<User> = emptyList(),
        val selectedFriendIds: Set<String> = emptySet(),
        val isLoadingFriends: Boolean = false,
        val loadError: String? = null,
    ) : CreateEventViewState()

    data class FillDetails(
        val selectedDate: String,
        val selectedFriends: List<User>,
        val title: String = "",
        val description: String = "",
        val location: String = "",
        val time: String = "",
        val isValidating: Boolean = false,
    ) : CreateEventViewState()

    data class Submitting(
        val selectedDate: String,
        val selectedFriends: List<User>,
        val title: String,
        val description: String?,
        val location: String?,
        val time: String?,
    ) : CreateEventViewState()

    data class Success(
        val event: Event,
    ) : CreateEventViewState()

    data class Error(
        val message: String,
        val previousState: CreateEventViewState? = null,
    ) : CreateEventViewState()
}
