package friends.mobile.feature.friends.presentation

import friends.mobile.feature.auth.data.remote.dto.UserDto
import friends.mobile.feature.friends.domain.model.PaginationInfo

/**
 * MVI view state for the friends screen.
 *
 * States:
 *   - Loading: fetching friends from the backend
 *   - Error: something went wrong (network, auth, etc.)
 *   - Content: successfully loaded friends list (may be empty)
 *
 * Properties:
 *   - friends: list of friends from the backend
 *   - isRequestPending: true when a friend request operation is in progress
 *   - requestError: error message from a failed friend request operation
 *   - searchResults: list of search results, null if no search performed, empty list if search yielded no results
 *   - isSearching: true when a search operation is in progress
 */
sealed class FriendsViewState {
    data object Loading : FriendsViewState()
    data class Error(val message: String) : FriendsViewState()
    data class Content(
        val friends: List<UserDto> = emptyList(),
        val isRequestPending: Boolean = false,
        val requestError: String? = null,
        val searchResults: List<UserDto>? = null,
        val isSearching: Boolean = false,
    ) : FriendsViewState()
}
