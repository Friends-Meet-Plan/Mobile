package friends.mobile.feature.friends.presentation

import friends.mobile.feature.auth.data.remote.dto.UserDto

/**
 * Enum representing the active tab in the friends screen.
 *
 * FRIENDS: my friends list
 * INCOMING: incoming friend requests
 * OUTGOING: outgoing friend requests
 */
enum class RequestTab {
    FRIENDS,
    INCOMING,
    OUTGOING,
}

/**
 * MVI view state for the friends screen.
 *
 * States:
 *   - Loading: fetching friends from the backend
 *   - Error: something went wrong (network, auth, etc.)
 *   - Content: successfully loaded friends list (may be empty)
 *
 * Properties:
 *   - currentTab: the currently active tab (FRIENDS, INCOMING, or OUTGOING)
 *   - friendsList: list of friends from the backend
 *   - incomingRequests: list of incoming friend requests
 *   - outgoingRequests: list of outgoing friend requests
 *   - isRequestPending: true when a friend request operation is in progress
 *   - requestError: error message from a failed friend request operation
 *   - searchResults: list of search results, null if no search performed, empty list if search yielded no results
 *   - isSearching: true when a search operation is in progress
 */
sealed class FriendsViewState {
    data object Loading : FriendsViewState()
    data class Error(val message: String) : FriendsViewState()
    data class Content(
        val currentTab: RequestTab = RequestTab.FRIENDS,
        val friendsList: List<UserDto> = emptyList(),
        val incomingRequests: List<UserDto> = emptyList(),
        val outgoingRequests: List<UserDto> = emptyList(),
        val isRequestPending: Boolean = false,
        val requestError: String? = null,
        val searchResults: List<UserDto>? = null,
        val isSearching: Boolean = false,
    ) : FriendsViewState()
}
